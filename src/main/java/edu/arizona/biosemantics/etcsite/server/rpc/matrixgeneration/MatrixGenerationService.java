package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.common.taxonomy.TaxonIdentification;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateFileFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.SetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.matrixgeneration.model.OntologySubclassProvenance;
import edu.arizona.biosemantics.matrixgeneration.model.OntologySuperclassProvenance;
import edu.arizona.biosemantics.matrixgeneration.model.Provenance;
import edu.arizona.biosemantics.matrixgeneration.model.SemanticMarkupProvenance;
import edu.arizona.biosemantics.matrixgeneration.model.TaxonomyDescendantInheritanceProvenance;
import edu.arizona.biosemantics.matrixgeneration.model.complete.AbsentPresentCharacter;
import edu.arizona.biosemantics.matrixgeneration.model.raw.CellValue;
import edu.arizona.biosemantics.matrixgeneration.model.raw.ColumnHead;
import edu.arizona.biosemantics.matrixgeneration.model.raw.Matrix;
import edu.arizona.biosemantics.matrixgeneration.model.raw.RowHead;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.server.MatrixFileUtil;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {
	
	private IFileService fileService;
	private IFileFormatService fileFormatService;
	private IFileAccessService fileAccessService;
	private IFilePermissionService filePermissionService;
	private Emailer emailer;
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Void>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Void>>();
	private Map<Integer, MatrixGeneration> activeMatrixGenerationProcess = new HashMap<Integer, MatrixGeneration>();
	private Map<Integer, Enhance> activeEnhanceProcess = new HashMap<Integer, Enhance>();
	private MatrixFileUtil matrixFileUtil = new MatrixFileUtil();
	private DAOManager daoManager;
	
	@Inject
	public MatrixGenerationService(IFileService fileService, IFileFormatService fileFormatService, IFileAccessService fileAccessService, 
			IFilePermissionService filePermissionService, Emailer emailer, 
			DAOManager daoManager) {
		this.daoManager = daoManager;
		this.fileService = fileService;
		this.fileFormatService = fileFormatService;
		this.fileAccessService = fileAccessService;
		this.filePermissionService = filePermissionService;
		this.emailer = emailer;
		executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Configuration.maxActiveMatrixGeneration));
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Task start(AuthenticationToken authenticationToken, String taskName, String input, String inputTermReview, String inputOntology,
			String taxonGroup, boolean inheritValues, boolean generateAbsentPresent) throws MatrixGenerationException {
		boolean isSharedInput = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), input);
		String inputFileName = null;
		try {
			inputFileName = fileService.getFileName(authenticationToken, input);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Permission denied to read " + input);
			throw new MatrixGenerationException();
		}
		if(isSharedInput) {
			String destination;
			try {
				destination = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
						inputFileName, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new MatrixGenerationException();
			}
			try {
				fileService.copyFiles(authenticationToken, input, destination);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new MatrixGenerationException();
			}
			input = destination;
		}
		
		if(inputTermReview != null && !inputTermReview.isEmpty()) {
			String inputTermReviewFileName = null;
			try {
				inputTermReviewFileName = fileService.getFileName(authenticationToken, inputTermReview);
			} catch (PermissionDeniedException e) {
				log(LogLevel.ERROR, "Permission denied to read " + inputTermReview);
				throw new MatrixGenerationException();
			}
			boolean isSharedInputTermReview = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), inputTermReview);
			if(isSharedInputTermReview) {
				String destination;
				try {
					destination = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
							inputTermReviewFileName, true);
				} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
					throw new MatrixGenerationException();
				}
				try {
					fileService.copyFiles(authenticationToken, inputTermReview, destination);
				} catch (CopyFilesFailedException | PermissionDeniedException e) {
					throw new MatrixGenerationException();
				}
				inputTermReview = destination;
			}
		}
	
		if(inputOntology != null && !inputOntology.isEmpty()) {
			boolean isSharedInputOntology = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), inputOntology);
			String inputOntologyFileName = null;
			try {
				inputOntologyFileName = fileService.getFileName(authenticationToken, inputOntology);
			} catch (PermissionDeniedException e) {
				log(LogLevel.ERROR, "Permission denied to read " + inputOntology);
				throw new MatrixGenerationException();
			}
			if(isSharedInputOntology) {
				String destination;
				try {
					destination = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
							inputOntologyFileName, true);
				} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
					throw new MatrixGenerationException();
				}
				try {
					fileService.copyFiles(authenticationToken, inputOntology, destination);
				} catch (CopyFilesFailedException | PermissionDeniedException e) {
					throw new MatrixGenerationException();
				}
				inputOntology = destination;
			}
		}
		
		MatrixGenerationConfiguration config = new MatrixGenerationConfiguration();
		config.setInput(input);
		config.setInputTermReview(inputTermReview);
		config.setInputOntology(inputOntology);
		TaxonGroup group = daoManager.getTaxonGroupDAO().getTaxonGroup(taxonGroup);
		config.setTaxonGroup(group);
		config.setOutput(config.getInput() + "_output_by_MG_task_" + taskName);
		config.setInheritValues(inheritValues);
		config.setGenerateAbsentPresent(generateAbsentPresent);
		config = daoManager.getMatrixGenerationConfigurationDAO().addMatrixGenerationConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.INPUT.toString());
		TinyUser user = daoManager.getUserDAO().getTinyUser(authenticationToken.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);
		
		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);

		try {
			fileService.setInUse(authenticationToken, true, input, task);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "can not set file in use for "+input+ ".");
			throw new MatrixGenerationException(task);
		}
		return task;
	}

	@Override
	public Task process(final AuthenticationToken token, final Task task) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		
		//browser back button may invoke another "process"
		if(activeProcessFutures.containsKey(config.getConfiguration().getId())) {
			return task;
		} else {			
			TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			daoManager.getTaskDAO().updateTask(task);
			
			try {
				fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, String.valueOf(task.getId()), false);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e1) {
				throw new MatrixGenerationException(task);
			}
			System.out.println(config.getTaxonGroup().getName()+"]]]]");
			if(config.getTaxonGroup().getName().equals("Bacteria")){
				doMatrixGeneration(token, task, config, config.getInput());
			}else{
				if(isEnhanceAndMatrixGeneration(config.getInputTermReview(), config.getInputOntology())) {
					//doMatrixGeneration(token, task, config, config.getInput());
					System.out.println("doEnhanceAndMatrixGeneration");
					doEnhanceAndMatrixGeneration(token, task, config);
				} else {
					System.out.println("doMinimalEnhanceAndMatrixGeneration");
					doMinimalEnhanceAndMatrixGeneration(token, task, config, config.getInput());
				}
			}
			
			return task;
		}
	}

	private boolean isEnhanceAndMatrixGeneration(String inputTermReview, String inputOntology) {
		return inputTermReview != null && inputOntology != null && 
				!inputTermReview.isEmpty() && !inputOntology.isEmpty();
	}

	private void doEnhanceAndMatrixGeneration(final AuthenticationToken token,  final Task task, final MatrixGenerationConfiguration config) throws MatrixGenerationException {
		String input = config.getInput();
		String inputOntology = config.getInputOntology();
		String ontologyFile = "";
		String inputTermReview = config.getInputTermReview();
		String categoryTerm = "";
		String synonym = "";
		String taxonGroup = config.getTaxonGroup().getName().toUpperCase();
		
		try {
			List<String> files = fileService.getDirectoriesFiles(token, inputOntology);
			for(String file : files) {
				if(file.endsWith(".owl") && !file.startsWith("module.") && !file.equals("ModifierOntology.owl")) {
					ontologyFile = inputOntology + File.separator + file;
				}	
			}
		} catch(PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		try {
			List<String> files = fileService.getDirectoriesFiles(token, inputTermReview);
			for(String file : files) {
				if(file.startsWith("category_term-")) {
					categoryTerm = inputTermReview + File.separator + file;
				}	
				if(file.startsWith("category_mainterm_synonymterm-")) {
					synonym = inputTermReview + File.separator + file;
				}
			}
		} catch(PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		
		
		final String enhanceDir = this.getTempDir(task) + File.separator + "enhance";
		final Enhance enhance = new ExtraJvmEnhance(input, enhanceDir, ontologyFile, categoryTerm, synonym, taxonGroup);
		//final Enhance enhance = new InJvmEnhance(input, enhanceDir, ontologyFile, categoryTerm, synonym, taxonGroup);
		
		activeEnhanceProcess.put(config.getConfiguration().getId(), enhance);
		final ListenableFuture<Void> futureResult = executorService.submit(enhance);
		this.activeProcessFutures.put(config.getConfiguration().getId(), futureResult);
		
		/*try {
			futureResult.get(Configuration.matrixGeneration_maxRunningTimeMinutes, TimeUnit.MINUTES);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		} catch (TimeoutException e2) {
			// Task took too long. 
			futureResult.cancel(true);
			matrixGeneration.destroy();
			log(LogLevel.ERROR,
					"Matrix generation took too long and was canceled.");
		}*/
		
		futureResult.addListener(new Runnable() {
		     	public void run() {	
		     		try {
		     			Enhance enhance = activeEnhanceProcess.remove(config.getConfiguration().getId());
		     			ListenableFuture<Void> futureResult = activeProcessFutures.remove(config.getConfiguration().getId());
		     			if(enhance.isExecutedSuccessfully()) {
		     				if(!futureResult.isCancelled()) {
		     					doMatrixGeneration(token, task, config, enhanceDir);
		     				}
		     			} else {
			     			task.setFailed(true);
							task.setFailedTime(new Date());
							task.setTooLong(futureResult.isCancelled());
							enhance.destroy();
							daoManager.getTaskDAO().updateTask(task);
			     		}
		     		} catch(Throwable t) {
		     			log(LogLevel.ERROR, t.getMessage()+"\n"+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
		     			task.setFailed(true);
						task.setFailedTime(new Date());
						enhance.destroy();
						daoManager.getTaskDAO().updateTask(task);
		     		}
		     	}
		     }, executorService);
		
	}

	private void doMinimalEnhanceAndMatrixGeneration(final AuthenticationToken token, final Task task, final MatrixGenerationConfiguration config, 
			String inputDir) throws MatrixGenerationException {
		String input = config.getInput();
		String ontologyFile = "";
		String categoryTerm = "";
		String synonym = "";
		String taxonGroup = config.getTaxonGroup().getName().toUpperCase();
		
		final String enhanceDir = this.getTempDir(task) + File.separator + "enhance";
		final Enhance enhance = new ExtraJvmEnhance(input, enhanceDir, ontologyFile, categoryTerm, synonym, taxonGroup);
		//final Enhance enhance = new InJvmEnhance(input, enhanceDir, ontologyFile, categoryTerm, synonym, taxonGroup);
		activeEnhanceProcess.put(config.getConfiguration().getId(), enhance);
		final ListenableFuture<Void> futureResult = executorService.submit(enhance);
		this.activeProcessFutures.put(config.getConfiguration().getId(), futureResult);
		
		/*try {
			futureResult.get(Configuration.matrixGeneration_maxRunningTimeMinutes, TimeUnit.MINUTES);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		} catch (TimeoutException e2) {
			// Task took too long. 
			futureResult.cancel(true);
			matrixGeneration.destroy();
			log(LogLevel.ERROR,
					"Matrix generation took too long and was canceled.");
		}*/
		
		futureResult.addListener(new Runnable() {
		     	public void run() {	
		     		try {
		     			Enhance enhance = activeEnhanceProcess.remove(config.getConfiguration().getId());
		     			ListenableFuture<Void> futureResult = activeProcessFutures.remove(config.getConfiguration().getId());
		     			if(enhance.isExecutedSuccessfully()) {
		     				if(!futureResult.isCancelled()) {
		     					doMatrixGeneration(token, task, config, enhanceDir);
		     				}
		     			} else {
			     			task.setFailed(true);
							task.setFailedTime(new Date());
							task.setTooLong(futureResult.isCancelled());
							enhance.destroy();
							daoManager.getTaskDAO().updateTask(task);
			     		}
		     		} catch(Throwable t) {
		     			log(LogLevel.ERROR, t.getMessage()+"\n"+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
		     			task.setFailed(true);
						task.setFailedTime(new Date());
						enhance.destroy();
						daoManager.getTaskDAO().updateTask(task);
		     		}
		     	}
		     }, executorService);
	}
	
	private void doMatrixGeneration(final AuthenticationToken token, final Task task, final MatrixGenerationConfiguration config, 
			String inputDir) throws MatrixGenerationException {
		String outputFile = getOutputFile(task);
		String taxonGroup = config.getTaxonGroup().getName().toUpperCase();
		boolean inheritValues = config.isInheritValues();
		boolean generateAbsentPresent = config.isGenerateAbsentPresent();
		//System.out.println(taxonGroup+"--"+"doMatrixGeneration");
		final MatrixGeneration matrixGeneration = new ExtraJvmMatrixGeneration(inputDir, taxonGroup, 
		outputFile, inheritValues, generateAbsentPresent, true);
		// server uses InJvmMatrixGeneration
		//final MatrixGeneration matrixGeneration = new InJvmMatrixGeneration(inputDir, taxonGroup, 
		//outputFile, inheritValues, generateAbsentPresent, true);
		
		activeMatrixGenerationProcess.put(config.getConfiguration().getId(), matrixGeneration);
		ListenableFuture<Void> futureResult = executorService.submit(matrixGeneration);
		activeProcessFutures.put(config.getConfiguration().getId(), futureResult);
		//System.out.println("run --"+"doMatrixGeneration");
		futureResult.addListener(new Runnable() {
	     	public void run() {	
	     		try {
	     			MatrixGeneration matrixGeneration = activeMatrixGenerationProcess.remove(config.getConfiguration().getId());
		    		ListenableFuture<Void> futureResult = activeProcessFutures.remove(config.getConfiguration().getId());
		     		if(matrixGeneration.isExecutedSuccessfully()) {
		     			if(!futureResult.isCancelled()) {
							TaskStage newTaskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.REVIEW.toString());
							task.setTaskStage(newTaskStage);
							task.setResumable(true);
							daoManager.getTaskDAO().updateTask(task);
								
							// send an email to the user who owns the task.
							sendFinishedGeneratingMatrixEmail(task);
							
							//convert to matrix_review
							loadMatrixFromProcessOutput(token, task);
		     			}
		     		} else {
		     			task.setFailed(true);
						task.setFailedTime(new Date());
						task.setTooLong(futureResult.isCancelled());
						matrixGeneration.destroy();
						daoManager.getTaskDAO().updateTask(task);
		     		}
		     	} catch(Throwable t) {
     			log(LogLevel.ERROR, t.getMessage()+"\n"+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
     			task.setFailed(true);
				task.setFailedTime(new Date());
				matrixGeneration.destroy();
				daoManager.getTaskDAO().updateTask(task);
     		}
	    	}
	     }, executorService);
	}

	@Override
	public Model loadMatrixFromProcessOutput(AuthenticationToken token, Task task) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		String outputFile = getOutputFile(task);
		TaxonMatrix matrix = null;

		Model model = new Model();
		try {
			//matrix = createTaxonMatrix(outputFile, model, config.getTaxonGroup().getName().equals("Bacteria"));//!
			matrix = createTaxonMatrix(outputFile, model, true);//!
		} catch (IOException | JDOMException | ClassNotFoundException e) {
			log(LogLevel.ERROR, "Couldn't create taxon matrix from generated output", e);
			throw new MatrixGenerationException(task);
		}
		
		if(matrix == null) 
			throw new MatrixGenerationException(task);
		
		model.setTaxonMatrix(matrix);
		try {
			serializeMatrix(model, Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser");
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't serialize matrix to disk", e);
			throw new MatrixGenerationException(task);
		}
		
		return model;
	}

	@Override
	public Model review(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException {
		try {
			Model model = unserializeMatrix(Configuration.matrixGeneration_tempFileBase + File.separator + 
					task.getId() + File.separator + "TaxonMatrix.ser");
			return model;
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.ERROR, "Couldn't get CSV from matrix", e);
			throw new MatrixGenerationException(task);
		}
	}
	
	@Override
	public Task completeReview(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException {
		TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.OUTPUT.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}
	
	@Override
	public Task output(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		config.setOutput(config.getInput() + "_output_by_MG_task_" + task.getName());
			
		String outputDirectory = config.getOutput();			
		String outputDirectoryParentResult;
		try {
			outputDirectoryParentResult = fileService.getParent(new AdminAuthenticationToken(), outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		String outputDirectoryNameResult;
		try {
			outputDirectoryNameResult = fileService.getFileName(new AdminAuthenticationToken(), outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
			
		//find a suitable destination filePath
		String createDirectoryResult;
		try {
			createDirectoryResult = fileService.createDirectory(new AdminAuthenticationToken(), outputDirectoryParentResult, 
				outputDirectoryNameResult, true);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new MatrixGenerationException(task);
		}
		config.setOutput(createDirectoryResult);
			
		String csvContent;
		try {
			csvContent = getCSVMatrix(unserializeMatrix(Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser").getTaxonMatrix());
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.ERROR, "Couldn't get CSV from matrix", e);
			throw new MatrixGenerationException(task);
		}
		
		try {
			fileService.copyFile(authenticationToken, 
					Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser", 
					config.getOutput() + File.separator + "TaxonMatrix.ser");
		} catch (CopyFilesFailedException e1) {
			throw new MatrixGenerationException(task);
		}
		
		try {
			String createFileResult = 
					fileService.createFile(new AdminAuthenticationToken(), createDirectoryResult, "Matrix.csv", true);
		} catch (CreateFileFailedException | PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		try {
			fileAccessService.setFileContent(new AdminAuthenticationToken(), 
					createDirectoryResult + File.separator + "Matrix.csv", csvContent);
		} catch (SetFileContentFailedException | PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		
		//update task
		task.setResumable(false);
		task.setComplete(true);
		task.setCompleted(new Date());
		daoManager.getTaskDAO().updateTask(task);
		
		daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectoryResult);
		
		return task;
	}
	
	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION)) {
						return task;
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
						
		//remove matrix generation configuration
		if(config.getInput() != null)
			try {
				fileService.setInUse(authenticationToken, false, config.getInput(), task);
			} catch (PermissionDeniedException e) {
				throw new MatrixGenerationException(task);
			}
		daoManager.getMatrixGenerationConfigurationDAO().remove(config);
		
		//remove task
		daoManager.getTaskDAO().removeTask(task);
		if(task.getConfiguration() != null)
			//remove configuration
			daoManager.getConfigurationDAO().remove(task.getConfiguration().getConfiguration());
	
		//cancel possible futures
		if(task.getTaskStage() != null) {
			switch(TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
			case INPUT:
				break;
			case PROCESS:
				if(activeProcessFutures.containsKey(config.getConfiguration().getId())) {
					ListenableFuture<Void> processFuture = this.activeProcessFutures.get(config.getConfiguration().getId());
					processFuture.cancel(true);
				}
				if(activeMatrixGenerationProcess.containsKey(config.getConfiguration().getId())) {
					activeMatrixGenerationProcess.get(config.getConfiguration().getId()).destroy();
				}
				if(activeEnhanceProcess.containsKey(config.getConfiguration().getId())) {
					activeEnhanceProcess.get(config.getConfiguration().getId()).destroy();
				}
				break;
			case OUTPUT:
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void save(AuthenticationToken authenticationToken, Model model, Task task) throws MatrixGenerationException {
		//final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		//String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.csv";
		try {
			serializeMatrix(model, Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser");
		} catch (IOException e) {
			throw new MatrixGenerationException(task);
		}
		//saveFile(matrix, outputFile);
	}
	
	@Override
	public Task goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {
		TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}
	
	@Override
	public String checkInputValid(AuthenticationToken authenticationToken, String filePath) {//TODO pass specific error messages to the users 
		boolean readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission)
			return "File Access Denied";
		boolean isDirectory = true;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return "File Access Denied";
		}
		if(!isDirectory)
			return "Invalid input: Input should be directory";
		List<String> files;
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return "Access Denied";
		}
		if(files.isEmpty())
			return "Invalid Input: Folder contains no files";
		
		//extra validation, since a valid taxon description is automatically also a valid marked up taxon description according to 
		//the schema. Check for min. 1 statement
		boolean statementFound = false;
		for(String file : files) {
			boolean valid;
			try {
				valid = fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, filePath + File.separator + file);
			} catch (PermissionDeniedException | GetFileContentFailedException e) {
				log(LogLevel.ERROR, "validation of "+file+ " threw exceptions");
				return "Access Denied: Validation exception";
			}

			if(!valid){
				log(LogLevel.ERROR, file+ " is not valid");
				return "File validation failed for "+ file;
			}
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document;
			try {
				document = saxBuilder.build(new File(filePath + File.separator + file));
			} catch (JDOMException | IOException e) {
				log(LogLevel.ERROR, "SAXBuilder cannot build "+(filePath + File.separator + file)+ ".");
				return "XML format error in file " + file;
			}
			XPathFactory xPathFactory = XPathFactory.instance();
			XPathExpression<Element> xPathExpression = 
					xPathFactory.compile("/bio:treatment/description[@type=\"morphology\"]/statement", Filters.element(), 
							null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics")); 
			if(!xPathExpression.evaluate(document).isEmpty()) {
				statementFound = true;
			}
			if(!statementFound){
				log(LogLevel.ERROR, "no statement found in file "+file+ ".");
				return "Input files should have at least one \\<statement\\>. Not found in "+ file;
			}
		}
		String result = fileService.validateTaxonNames(authenticationToken, filePath);
		if(!result.equals("success")){
			return result;
		}
		return "valid";
	}

	@Override
	public String outputMatrix(AuthenticationToken authenticationToken, Task task, Model model, MatrixFormat format) throws MatrixGenerationException {
		
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		
		String extName = ".csv";
		switch(format){
			case CSV: extName = ".csv"; break;
			case MCCSV: extName = ".csv"; break;
			case NEXUS: extName = ".nex"; break;
			case NEXML: extName = ".xml"; break;
		}
		String path = Configuration.matrixReview_tempFileBase + 
				File.separator + "matrix-review" + File.separator + authenticationToken.getUserId() +
				File.separator + "matrix_task-" + task.getName() + extName;
		//System.out.println(path);
		/*
		File file = new File(path);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't create output file", e);
			throw new MatrixGenerationException(task);
		}
		
		int columns = model.getTaxonMatrix().getCharacterCount() + 1;
		String[] characters = new String[columns];
		List<Character> flatCharacters = model.getTaxonMatrix().getFlatCharacters();
		characters[0] = "Taxa/Characters";
		int i=1;
		for(Character character : flatCharacters) {
			if(model.getTaxonMatrix().isVisiblyContained(character)) 
				characters[i++] = character.toString();
		}
		//CSVWriter writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8")), ',',CSVWriter.NO_QUOTE_CHARACTER);	
		
		//outputStream.write(239);
		//outputStream.write(187);
		//outputStream.write(191);
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(characters);
			for(Taxon taxon : model.getTaxonMatrix().getFlatTaxa()) {
				String[] line = new String[columns];
				line[0] = taxon.getBiologicalName();
				i = 1;
				for(Character character : flatCharacters) {
					if(model.getTaxonMatrix().isVisiblyContained(character)) 
						line[i++] = model.getTaxonMatrix().getValue(taxon, character).toString();
				}
				csvWriter.writeNext(line);
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't open or close writer", e);
			throw new MatrixGenerationException(task);
		}
		
		/*ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String jsonModel = writer.writeValueAsString(model);
		try (FileWriter fileWriter = new FileWriter(new File(path))) {
			fileWriter.write(jsonModel);
		}*/
		try{
			switch(format){
				case CSV: matrixFileUtil.generateSimpleCSV(path, model.getTaxonMatrix());break;
				case MCCSV: matrixFileUtil.generateMatrixConverterCSV(path, model.getTaxonMatrix());break;
				case NEXUS: break;
				case NEXML: break;
				default: matrixFileUtil.generateMatrixConverterCSV(path, model.getTaxonMatrix());
			}
		} catch(Exception e){
			log(LogLevel.ERROR, "Couldn't download matrix", e);
			throw new MatrixGenerationException(task);
		}
		return path;
	}
	
	@Override
	public void destroy() {
		this.executorService.shutdownNow();
		for(MatrixGeneration matrixGeneration : activeMatrixGenerationProcess.values()) {
			matrixGeneration.destroy();
		}
		for(Enhance enhance : activeEnhanceProcess.values()) {
			enhance.destroy();
		}
		super.destroy();
	}
	
	private void sendFinishedGeneratingMatrixEmail(Task task) {
		User user = daoManager.getUserDAO().getUser(task.getUser().getId());
		if (user.getProfileValue(User.EmailPreference.MATRIX_GENERATION.getKey())) {
			String subject = Configuration.finishedMatrixgenerationGenerateSubject.replace("<taskname>", task.getName());
			String body = Configuration.finishedMatrixgenerationGenerateBody.replace("<taskname>", task.getName());
			emailer.sendEmail(user.getEmail(), subject, body);
		}
	}

	//create taxon matrix from raw matrix which is generated by matrix generation
	public TaxonMatrix createTaxonMatrix(String filePath, Model model, boolean showWholeOrganismReference) throws ClassNotFoundException, IOException, JDOMException {
		List<Organ> hierarhicalCharacters = new LinkedList<Organ>();
		List<Taxon> hierarchyTaxa = new LinkedList<Taxon>();
		
	    try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath))) {
	    	Matrix rawMatrix = (Matrix)objectInputStream.readObject();
	    	
	    	List<edu.arizona.biosemantics.matrixgeneration.model.complete.Taxon> taxa = rawMatrix.getSource().getTaxa();
//			for(edu.arizona.biosemantics.matrixgeneration.model.complete.Value v : rawMatrix.getSource().getValues())
//				System.out.println(v.getValue()+"===>"+v.getStatement().getText());
			
			
	    	edu.arizona.biosemantics.matrixgeneration.model.complete.Matrix completeMatrix = rawMatrix.getSource();
	    	List<Character> charactersInMatrix = new LinkedList<Character>();
	    	HashMap<String, Organ> organMap = new HashMap<String, Organ>();
	    	List<ColumnHead> columnHeads = rawMatrix.getColumnHeads();
	    	
	    	Map<Taxon, edu.arizona.biosemantics.matrixgeneration.model.complete.Taxon> taxonTaxonMap = 
	    			new HashMap<Taxon, edu.arizona.biosemantics.matrixgeneration.model.complete.Taxon>();
	    	Map<Character, edu.arizona.biosemantics.matrixgeneration.model.complete.Character> characterCharacterMap = 
	    			new HashMap<Character, edu.arizona.biosemantics.matrixgeneration.model.complete.Character>();
	    	for(int i=0; i<columnHeads.size(); i++) {
	    		ColumnHead columnHead = columnHeads.get(i);
	    		
	    		String characterName = columnHead.getSource().getName();
	    		String connector = columnHead.getSource().getConnector();
	    		edu.arizona.biosemantics.matrixgeneration.model.complete.Character chara = columnHead.getSource();
	    		String organName = chara.getBearerStructureIdentifier().getDisplayName();
	    		if(organName.equals("whole_organism") && !showWholeOrganismReference) {
	    			organName = "";
	    			connector = "";
	    		}
				Organ o;
				if(!organMap.containsKey(organName)) {
					o = new Organ(organName);
					organMap.put(organName, o);
					hierarhicalCharacters.add(o);
				} else {
					o = organMap.get(organName);
				}

				Character c = new Character(characterName, connector, o, o.getFlatCharacters().size());
				charactersInMatrix.add(c);
				characterCharacterMap.put(c, columnHead.getSource());
	    	}
	    	
	    	List<TaxonIdentification> taxonIdentifications = new LinkedList<TaxonIdentification>();
	    	Map<TaxonIdentification, Taxon> rankTaxaMap = new HashMap<TaxonIdentification, Taxon>();
	    	Map<Taxon, RowHead> taxonRowHeadMap = new HashMap<Taxon, RowHead>();
	    	for(RowHead rowHead : rawMatrix.getRowHeads()) {
	    		 TaxonIdentification taxonIdentification = 
	    				 rowHead.getSource().getTaxonIdentification();
	    		 taxonIdentifications.add(taxonIdentification);
	    		 String description = getMorphologyDescription(rowHead.getSource().getSourceFile());
	    
	    		 List<String> statementStrings = rowHead.getSource().getStatementString();
	    		 //TODO: Integrate rank authority and date
	    		 Taxon taxon = new Taxon(taxonIdentification, description);
	    		 taxon.setStatements(statementStrings);
	    		 
	    		 rankTaxaMap.put(taxonIdentification, taxon);
	    		 taxonRowHeadMap.put(taxon, rowHead);
	    		 taxonTaxonMap.put(taxon, rowHead.getSource());
	    	 }
	    	
	    	for(TaxonIdentification taxonIdentification : taxonIdentifications) {
	    		LinkedList<RankData> rankData = taxonIdentification.getRankData();
		    	Taxon taxon = rankTaxaMap.get(taxonIdentification);
		    	if(rankData.size() == 1) 
		    		hierarchyTaxa.add(taxon);
			    if(rankData.size() > 1) {
			    	//int parentRankIndex = rankData.size() - 2;
			    	Taxon parentTaxon = null;
			    	//while(parentTaxon == null && parentRankIndex >= 0) {
		    		LinkedList<RankData> parentRankDatas = new LinkedList<RankData>(rankData);
		    		while(parentRankDatas.size() > 1) {
			    		parentRankDatas.removeLast();
			    		TaxonIdentification parentTaxonIdentificaiton = new TaxonIdentification(parentRankDatas, 
			    				taxonIdentification.getAuthor(), taxonIdentification.getDate());
				    	//RankData parentRankData = rankData.get(parentRankIndex);
			    		parentTaxon = rankTaxaMap.get(parentTaxonIdentificaiton);
			    		//parentRankIndex--;
						if(parentTaxon != null)
							break;
			    	}
			    	if(parentTaxon == null)
			    		hierarchyTaxa.add(taxon);
			    	else
			    		parentTaxon.addChild(taxon);
			    }
		    }
	    	
		    TaxonMatrix taxonMatrix = new TaxonMatrix(hierarhicalCharacters, hierarchyTaxa);
		    
		    Color inheritedColor = new Color("FFD700", "Inherited");
		    Color conflictColor = new Color("FF0000", "Conflict");
		    Color sourceColor = new Color("00FFFF", "Source");
		    Color ontologyColor = new Color("FF00FF", "Ontology");
			model.getColors().add(inheritedColor);
			model.getColors().add(conflictColor);
			model.getColors().add(sourceColor);
			model.getColors().add(ontologyColor);
		    //set values
		    for(Taxon taxon : taxonMatrix.getFlatTaxa()) {
		    	RowHead rowHead = taxonRowHeadMap.get(taxon);
		    	//System.out.println("taxon " + rowHead.getValue());
		    	for(int j=0; j<charactersInMatrix.size(); j++) {
		    		Character character = charactersInMatrix.get(j);
		    		CellValue cellValue = rawMatrix.getCellValues().get(rowHead).get(j);
		    		
		    		//System.out.println("character " + character.getName() + " value " + cellValue.getText());
		    		//System.out.println(cellValue.getGenerationProvenance());
		    		
		    		String value = cellValue.getText();
		    		Value v = new Value(value);
		    		
		    		for(edu.arizona.biosemantics.matrixgeneration.model.complete.Value cvalue : cellValue.getSource()) {
		    			//if(cvalue == null) 
		    			//	System.out.println("null cvalue:"+cvalue);
		    			//else
		    			//	System.out.println(cvalue.getGenerationProvenance());
		    			if(cvalue!=null) v.addValueStatement(cvalue.getValue(), cvalue.getStatement().getText());
		    			//System.out.println("character " + character.getName() + " value " + cvalue.getValue()+" SOURCE:"+cvalue.getStatement().getText());
		    		}
		    		
		    		Set<Provenance> provenanceSet = new HashSet<Provenance>();
		    		provenanceSet.addAll(rawMatrix.getCellValues().get(rowHead).get(j).getGenerationProvenance());		    		
		    		for(edu.arizona.biosemantics.matrixgeneration.model.complete.Value completeValue : 
		    			rawMatrix.getCellValues().get(rowHead).get(j).getSource()) {
		    			if(completeValue != null) {
		    				if(completeValue.getGenerationProvenance() != null) 
		    					provenanceSet.addAll(completeValue.getGenerationProvenance());
		    			}
		    		}
		    		for(Provenance provenance : provenanceSet) {
		    			if(provenance.getSource().equals(edu.arizona.biosemantics.matrixgeneration.transform.complete.AbsentPresentFromBiologicalEntitiesTransformer.class)) {
		    				model.setColor(v, sourceColor);
		    			}
		    			if(provenance.getSource().equals(edu.arizona.biosemantics.matrixgeneration.transform.complete.AbsentPresentFromRelationsTranformer.class)) {
		    				model.setColor(v, sourceColor);
		    			}
		    			if(provenance instanceof TaxonomyDescendantInheritanceProvenance) {
		    				model.setColor(v, inheritedColor);
		    				//System.out.println("inherited");
		    			}
		    			if(provenance instanceof SemanticMarkupProvenance) {
		    				SemanticMarkupProvenance semanticMarkupProvenance = (SemanticMarkupProvenance)provenance;
		    				if(!taxonTaxonMap.get(taxon).equals(semanticMarkupProvenance.getTaxon()) ||
		    						!characterCharacterMap.get(character).equals(semanticMarkupProvenance.getCharacter())) {
		    				} else {
		    					model.setColor(v, sourceColor);
			    				//System.out.println("source");
		    				}
		    			}
		    			if(provenance instanceof OntologySubclassProvenance) {
		    				model.setColor(v, ontologyColor);
		    				//System.out.println("ontologycolor");
		    			}
		    			if(provenance instanceof OntologySuperclassProvenance) {
		    				model.setColor(v, ontologyColor);
		    				//System.out.println("ontologycolor");
		    			}
		    		}
		    		
		    		if(rawMatrix.getColumnHeads().get(j).getSource() instanceof AbsentPresentCharacter) {
		    			if(cellValue.getContainedValues().contains("present") && 
			    				cellValue.getContainedValues().contains("absent")) {
			    			model.setColor(v, conflictColor);
			    			//System.out.println("conflict");
			    		}
		    		}
		    		
		    		taxonMatrix.setValue(taxon, character, v);
		    	}
		    } 
		    
			return taxonMatrix;
	    }
	}

	private String getMorphologyDescription(File file) throws JDOMException, IOException {
		StringBuilder result = new StringBuilder();
		SAXBuilder sax = new SAXBuilder();
		Document doc = sax.build(file);
		
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//description[@type='morphology']", Filters.element(), null,
				Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
		for(Element element : xp.evaluate(doc)) {
			List<Element> statements = element.getChildren("statement");
			for(Element statement : statements) {
				Element text = statement.getChild("text");
				result.append(text.getText() + " ");
			}
			result.append("\n\n");
		}
		return result.toString();
	}

	private TaxonIdentification createTaxonIdentification(String name, Map<RankData, RankData> rankDataInstances) {
		String[] ranksAuthorParts = name.split(":");
    	String[] authorParts = ranksAuthorParts[1].split(",");
    	String author = authorParts[0];
    	String date = authorParts[1];
    	
    	String[] ranks = ranksAuthorParts[0].split(";");
    	LinkedList<RankData> rankData = new LinkedList<RankData>();
    	
    	RankData parent = null;
    	for(String rankString : ranks) {
    		String[] rankParts = rankString.split(",");
    		String authority = "";
    		String rankDate = "";
    		String value = "";
    		Rank rank = Rank.UNRANKED;
    		for(String rankPart : rankParts) {
        		String[] rankPartValues = rankPart.split("=");
        		if(rankPartValues[0].equals("authority")) {
        			authority = rankPartValues[1];
        			continue;
        		}
        		if(rankPartValues[0].equals("date")) {
        			rankDate = rankPartValues[1];
        			continue;
        		}
        		try {
        			rank = Rank.valueOf(rankPartValues[0].toUpperCase());
        			value = rankPartValues[1];
        		} catch(Exception e) {
        		}
        	}
    		
    		RankData rankDataInstance = new RankData(rank, value, parent, authority, date);
			if(!rankDataInstances.containsKey(rankDataInstance))
				rankDataInstances.put(rankDataInstance, rankDataInstance);
			rankDataInstance = rankDataInstances.get(rankDataInstance);
    		rankData.add(rankDataInstance);
    		parent = rankDataInstance;
    	}
    			
		TaxonIdentification result = new TaxonIdentification(rankData, author.split("=")[1], date.split("=")[1]);
		return result;
	}
	
	private String getCSVMatrix(TaxonMatrix matrix) throws IOException {
		//TODO: Add RankData authority and date
		try(StringWriter stringWriter = new StringWriter()) {
			try(CSVWriter writer = new CSVWriter(stringWriter)) {
			
				String[] line = new String[matrix.getCharacterCount() + 1];
				line[0] = "Name";
				ArrayList<Character> sortedArray = sortCharacters(matrix.getFlatCharacters());
				for(int i=1; i<=sortedArray.size(); i++) {
					line[i] = sortedArray.get(i - 1).toString();
				}
				writer.writeNext(line);
				
				for(Taxon taxon : matrix.getHierarchyTaxaDFS()) {
					line = new String[matrix.getCharacterCount() + 1];
					line[0] = getTaxonName(taxon);
					
					for(int j=1; j<matrix.getCharacterCount(); j++) {
						Character character = sortedArray.get(j - 1);
						line[j] = matrix.getValue(taxon, character).toString();
					}
					writer.writeNext(line);
				}
				writer.flush();
				String result = stringWriter.toString();
				return result;
			}
		}
	}

	private ArrayList<Character> sortCharacters(List<Character> flatCharacters) {
		ArrayList<Character> sortedArray = new ArrayList<Character>();
		ArrayList<String> sortedOrgans = new ArrayList<String>();
		HashMap<String, ArrayList<Character>> organsMap = new HashMap<String, ArrayList<Character>>();
		for(Character character: flatCharacters){
			String organ = character.getOrgan().toString();
			if(!sortedOrgans.contains(organ))
				sortedOrgans = insertOrgan(organ, sortedOrgans);
			if(organsMap.containsKey(organ)){
				organsMap = insertCharacter(organ, character, organsMap);
			}else{
				ArrayList<Character> subList = new ArrayList<Character>();
				subList.add(character);
				organsMap.put(organ, subList);
			}
		}
		for(String organ : sortedOrgans){
			sortedArray.addAll(organsMap.get(organ));
		}
		
		return sortedArray;
	}

	private HashMap<String, ArrayList<Character>> insertCharacter(String organ, Character character,
			HashMap<String, ArrayList<Character>> organsMap) {
		// TODO Auto-generated method stub
		ArrayList<Character> subList = organsMap.get(organ);
		if(subList.isEmpty()){
			subList.add(character);
			organsMap.put(organ, subList);
			return organsMap;
		}
		boolean success = false;
		int index = 0;
		for(Character characterItem: subList){
			if(character.getName().compareTo(characterItem.getName()) < 0){
				subList.add(index, character);
				success = true;
				break;
			}
			index ++;
		}
		if(!success){
			subList.add(character);
		}
		organsMap.put(organ, subList);
		return organsMap;
	}

	private ArrayList<String> insertOrgan(String organ, ArrayList<String> sortedOrgans) {
		// TODO Auto-generated method stub
		if(sortedOrgans.isEmpty()){
			sortedOrgans.add(organ);
			return sortedOrgans;
		}
		boolean success = false;
		int index=0;
		for(String sortedOrgan: sortedOrgans){
			if(organ.compareTo(sortedOrgan) < 0){
				sortedOrgans.add(index, organ);
				success = true;
				break;
			}
			index++;
		}
		if(!success){
			sortedOrgans.add(organ);
		}
		return sortedOrgans;
	}

	private String getTaxonName(Taxon taxon) {
		//return taxon.getFullName();
		
		/*String name = "";
		ArrayList<Taxon> taxonPath = new ArrayList<Taxon>();
		Taxon current = taxon;
		taxonPath.add(taxon);
		while(current.hasParent()) {
			current = current.getParent();
			taxonPath.add(current);
		}
		
		ListIterator<Taxon> it = taxonPath.listIterator(taxonPath.size());
		while(it.hasPrevious()) {
			current = it.previous();
			name += current.getRank() + "=" + current.getName() + ";";
		}
		name = name.substring(0, name.length() - 1);
		name += ":";
		name += "author=" + taxon.getAuthor() + ",";
		name += "date=" + taxon.getYear();
	
		return name;*/
		
		String name = "";
		
		LinkedList<RankData> rankDatas = taxon.getTaxonIdentification().getRankData();
		for(RankData rankData : rankDatas) {
			name += rankData.getRank() + "=" + rankData.getName() + ";";
		}
		name = name.substring(0, name.length() - 1);
		name += ":";
		name += "author=" + taxon.getAuthor() + ",";
		name += "date=" + taxon.getYear();
	
		return name;
	}


	
	private void serializeMatrix(Model model, String file) throws IOException {
		try(ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			output.writeObject(model);
		}
	}
	
	private Model unserializeMatrix(String file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			Model model = (Model)input.readObject();
			return model;
		}
	}
	
	private MatrixGenerationConfiguration getMatrixGenerationConfiguration(Task task) throws MatrixGenerationException {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof MatrixGenerationConfiguration))
			throw new MatrixGenerationException(task);
		final MatrixGenerationConfiguration mtrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
		return mtrixGenerationConfiguration;
	}

	private String getTempDir(Task task) {
		return Configuration.matrixGeneration_tempFileBase + File.separator + task.getId();
	}
	
	private String getOutputFile(Task task) {
		return Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.ser";
	}
	
	/*private TaxonMatrix createSampleMatrix() {
		List<Character> characters = new LinkedList<Character>();
		Organ o1 = new Organ("stem");
		Organ o2 = new Organ("leaf");
		Organ o3 = new Organ("head");
		Character a1 = new Character("length", o1);
		Character a2 = new Character("shape", o1);
		Character a3 = new Character("architecture", o1);
		
		Character b1 = new Character("width", o2);
		Character b2 = new Character("shape", o2);
		Character b3 = new Character("pubescence", o2);
		
		Character c1 = new Character("size", o3);
		Character c2 = new Character("color", o3);
		Character c3 = new Character("architecture", o3);
		
		
		characters.add(a1);
		characters.add(a2);
		characters.add(a3);
		characters.add(b1);
		characters.add(b2);
		characters.add(b3);
		characters.add(c1);
		characters.add(c2);
		characters.add(c3);
		
		for(int i=0; i<20; i++) {
			Character o = new Character("o" + i, o2);
			characters.add(o);
		}
		
		TaxonMatrix taxonMatrix = new TaxonMatrix(characters);
	
		for(int i=0; i<1; i++) {
			Taxon t1 = new Taxon("server" + i * 4 + 1, Rank.FAMILY, "rosacea", "author1", "1979", "this is the description about t1");
			Taxon t2 = new Taxon("server" +  i * 4 + 2, Rank.GENUS, "rosa", "author2", "1985",  "this is the description about t2");
			Taxon t3 = new Taxon("server" +  i * 4 + 3, Rank.SPECIES,
					"example", "author3", "2002", 
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
							+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
							+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
							+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
							+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
							+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
							+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
							+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
							+ "tincidunt diam nec urna. Curabitur velit.");
			Taxon t4 = new Taxon("server" +  i * 4 + 4, Rank.VARIETY,
					"prototype", "author4", "2014", 
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
							+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
							+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
							+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
							+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
							+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
							+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
							+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
							+ "tincidunt diam nec urna. Curabitur velit.");
			
			taxonMatrix.addRootTaxon(t1);
			taxonMatrix.addTaxon(t1, t2);
			taxonMatrix.addTaxon(t2, t3);
			taxonMatrix.addTaxon(t2, t4);
	
			Random random = new Random();
			taxonMatrix.setValue(t1, b1, new Value(String.valueOf(random.nextInt(50))));
		}
		
		/*for(int i=5; i<50; i++) {
			Taxon t5 = new Taxon("server" + i, Level.SPECIES, "t123", "a", "2", "de");
			taxonMatrix.addRootTaxon(t4);
		}*/
		
	/*	return taxonMatrix;
	}*/
	
	public static void main(String[] args) throws Exception {
		//MatrixGenerationService service = new MatrixGenerationService(null, null, null, null, null, null);
		//service.createTaxonMatrix("C:/Users/rodenhausen.CATNET/Desktop/Matrix.ser", new Model());
		edu.arizona.biosemantics.matrixgeneration.io.raw.out.CSVWriter writer = new 
				edu.arizona.biosemantics.matrixgeneration.io.raw.out.CSVWriter("final1.csv", false);
		 try(ObjectInputStream objectInputStream = new ObjectInputStream(
				 new FileInputStream("C:/Users/rodenhausen.CATNET/workspace/replaceOperator/out2/Matrix.ser"))) {
		    	Matrix rawMatrix = (Matrix)objectInputStream.readObject();
				writer.write(rawMatrix);
		 }
	}
	
	@Override
	public List<Task> getResumables(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getResumableTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION)) {
				result.add(task);
			}
		}
		return result;
	}
	
	@Override
	public void publish(AuthenticationToken token, Task task) throws Exception {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		File outputDir = new File(config.getOutput());
		File publicDir = new File(Configuration.publicFolder + File.separator + outputDir.getName());
		fileService.copyDirectory(token, config.getOutput(), Configuration.publicFolder + File.separator + outputDir.getName());
		FileUtils.copyDirectory(outputDir, publicDir);
	}
}
