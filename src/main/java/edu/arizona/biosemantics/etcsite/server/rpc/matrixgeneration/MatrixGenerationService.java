package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.ParseResult;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.common.taxonomy.TaxonIdentification;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
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
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
import edu.arizona.biosemantics.matrixgeneration.model.raw.ColumnHead;
import edu.arizona.biosemantics.matrixgeneration.model.raw.Matrix;
import edu.arizona.biosemantics.matrixgeneration.model.raw.RowHead;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {
	
	private IFileService fileService = new FileService();
	private IFileFormatService fileFormatService = new FileFormatService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Void>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Void>>();
	private Map<Integer, MatrixGeneration> activeProcess = new HashMap<Integer, MatrixGeneration>();
	private DAOManager daoManager = new DAOManager();
	private Emailer emailer = new Emailer();
	
	public MatrixGenerationService() {
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
	public Task start(AuthenticationToken authenticationToken, String taskName, String input, boolean inheritValues, boolean generateAbsentPresent) throws MatrixGenerationException {	
		boolean isShared = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), input);
		String fileName = null;
		try {
			fileName = fileService.getFileName(authenticationToken, input);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Permission denied to read "+fileName);
			throw new MatrixGenerationException();
		}
		if(isShared) {
			String destination;
			try {
				destination = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
						fileName, true);
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
		
		MatrixGenerationConfiguration config = new MatrixGenerationConfiguration();
		config.setInput(input);	
		config.setOutput(config.getInput() + "_output_by_MG_task_" + taskName);
		config.setInheritValues(inheritValues);
		config.setGenerateAbsentPresent(generateAbsentPresent);
		config = daoManager.getMatrixGenerationConfigurationDAO().addMatrixGenerationConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.INPUT.toString());
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
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
	public Task process(final AuthenticationToken authenticationToken, final Task task) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		
		//browser back button may invoke another "learn"
		if(activeProcessFutures.containsKey(config.getConfiguration().getId())) {
			return task;
		} else {
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			daoManager.getTaskDAO().updateTask(task);
			
			String input = config.getInput();			
			try {
				fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, String.valueOf(task.getId()), false);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e1) {
				throw new MatrixGenerationException(task);
			}
			final String outputFile = getOutputFile(task);
			
			boolean inheritValues = config.isInheritValues();
			boolean generateAbsentPresent = config.isGenerateAbsentPresent();
			
			
			final MatrixGeneration matrixGeneration = new ExtraJvmMatrixGeneration(input, outputFile, inheritValues, generateAbsentPresent, true);
			//final MatrixGeneration matrixGeneration = new InJvmMatrixGeneration(input, outputFile, inheritValues, generateAbsentPresent, true);
			activeProcess.put(config.getConfiguration().getId(), matrixGeneration);
			final ListenableFuture<Void> futureResult = executorService.submit(matrixGeneration);
			this.activeProcessFutures.put(config.getConfiguration().getId(), futureResult);
			futureResult.addListener(new Runnable() {
			     	public void run() {	
			     		try {
				     		MatrixGeneration matrixGeneration = activeProcess.remove(config.getConfiguration().getId());
				    		ListenableFuture<Void> futureResult = activeProcessFutures.remove(config.getConfiguration().getId());
				     		if(matrixGeneration.isExecutedSuccessfully()) {
				     			if(!futureResult.isCancelled()) {
									TaskStage newTaskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.REVIEW.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									daoManager.getTaskDAO().updateTask(task);
									
									// send an email to the user who owns the task.
									sendFinishedGeneratingMatrixEmail(task);
									
									loadMatrixFromProcessOutput(authenticationToken, task);
				     			}
				     		} else {
				     			task.setFailed(true);
								task.setFailedTime(new Date());
								daoManager.getTaskDAO().updateTask(task);
				     		}
			     		} catch(Throwable t) {
			     			log(LogLevel.ERROR, t.getMessage()+"\n"+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
			     			task.setFailed(true);
							task.setFailedTime(new Date());
							daoManager.getTaskDAO().updateTask(task);
			     		}
			     	}
			     }, executorService);
			
			return task;
		}
	}

	
	@Override
	public Model loadMatrixFromProcessOutput(AuthenticationToken token, Task task) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		String outputFile = getOutputFile(task);
		TaxonMatrix matrix = null;
		try {
			matrix = createTaxonMatrix(outputFile);
		} catch (IOException | JDOMException | ClassNotFoundException e) {
			log(LogLevel.ERROR, "Couldn't create taxon matrix from generated output", e);
			throw new MatrixGenerationException(task);
		}
		
		if(matrix == null) 
			throw new MatrixGenerationException(task);
		
		Model model = new Model(matrix);
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
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION);
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
			outputDirectoryParentResult = fileService.getParent(authenticationToken, outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		String outputDirectoryNameResult;
		try {
			outputDirectoryNameResult = fileService.getFileName(authenticationToken, outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
			
		//find a suitable destination filePath
		String createDirectoryResult;
		try {
			createDirectoryResult = fileService.createDirectory(authenticationToken, outputDirectoryParentResult, 
				outputDirectoryNameResult, true);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new MatrixGenerationException(task);
		}
			
		String csvContent;
		try {
			csvContent = getCSVMatrix(unserializeMatrix(Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser").getTaxonMatrix());
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.ERROR, "Couldn't get CSV from matrix", e);
			throw new MatrixGenerationException(task);
		}
		try {
			String createFileResult = 
					fileService.createFile(new AdminAuthenticationToken(), createDirectoryResult, "Matrix.csv", true);
		} catch (CreateFileFailedException | PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		try {
			fileAccessService.setFileContent(authenticationToken, 
					createDirectoryResult + File.separator + "Matrix.csv", csvContent);
		} catch (SetFileContentFailedException | PermissionDeniedException e) {
			throw new MatrixGenerationException(task);
		}
		
		//update task
		config.setOutput(createDirectoryResult);
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
			if(task != null && task.isResumable() && 
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
				if(activeProcess.containsKey(config.getConfiguration().getId())) {
					activeProcess.get(config.getConfiguration().getId()).destroy();
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
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION);
		TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}
	
	@Override
	public boolean isValidInput(AuthenticationToken authenticationToken, String filePath) {//TODO pass specific error messages to the users 
		boolean readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission)
			return false;
		boolean isDirectory = true;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return false;
		}
		if(!isDirectory)
			return false;
		List<String> files;
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return false;
		}
		
		//extra validation, since a valid taxon description is automatically also a valid marked up taxon description according to 
		//the schema. Check for min. 1 statement
		boolean statementFound = false;
		for(String file : files) {
			boolean valid;
			try {
				valid = fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, filePath + File.separator + file);
			} catch (PermissionDeniedException | GetFileContentFailedException e) {
				log(LogLevel.ERROR, "validation of "+file+ " threw exceptions");
				return false;
			}

			if(!valid){
				log(LogLevel.ERROR, file+ " is not valid");
				return false;
			}
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document;
			try {
				document = saxBuilder.build(new File(filePath + File.separator + file));
			} catch (JDOMException | IOException e) {
				log(LogLevel.ERROR, "SAXBuilder cannot build "+(filePath + File.separator + file)+ ".");
				return false;
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
			}
		}
		return statementFound;
	}

	@Override
	public String outputMatrix(AuthenticationToken authenticationToken, Task task, Model model) throws MatrixGenerationException {
		final MatrixGenerationConfiguration config = getMatrixGenerationConfiguration(task);
		
		String path = Configuration.tempFiles + 
				File.separator + "matrix-review" + File.separator + authenticationToken.getUserId() +
				File.separator + "matrix_task-" + task.getName() + ".csv";
		File file = new File(path);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't create output file", e);
			throw new MatrixGenerationException(task);
		}
		
		String[] characters = new String[model.getTaxonMatrix().getCharacterCount() + 1];
		List<Character> flatCharacters = model.getTaxonMatrix().getFlatCharacters();
		int columns = flatCharacters.size() + 1;
		characters[0] = "Taxa/Characters";
		for(int i=0; i<flatCharacters.size(); i++) {
			characters[i+1] = flatCharacters.get(i).toString();
		}
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(characters);
			for(Taxon taxon : model.getTaxonMatrix().getFlatTaxa()) {
				String[] line = new String[columns];
				line[0] = taxon.getFullName();
				for(int i=0; i<flatCharacters.size(); i++) {
					Character character = flatCharacters.get(i);
					line[i+1] = model.getTaxonMatrix().getValue(taxon, character).toString();
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
		return path;
	}
	
	@Override
	public void destroy() {
		this.executorService.shutdownNow();
		for(MatrixGeneration matrixGeneration : activeProcess.values()) {
			matrixGeneration.destroy();
		}
		super.destroy();
	}
	
	private void sendFinishedGeneratingMatrixEmail(Task task){
		String email = daoManager.getUserDAO().getUser(task.getUser().getId()).getEmail();
		String subject = Configuration.finishedMatrixgenerationGenerateSubject.replace("<taskname>", task.getName());
		String body = Configuration.finishedMatrixgenerationGenerateBody.replace("<taskname>", task.getName());
		
		emailer.sendEmail(email, subject, body);
	}

	private TaxonMatrix createTaxonMatrix(String filePath) throws ClassNotFoundException, IOException, JDOMException {
		List<Organ> hierarhicalCharacters = new LinkedList<Organ>();
		List<Taxon> hierarchyTaxa = new LinkedList<Taxon>();
		
	    try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath))) {
	    	Matrix rawMatrix = (Matrix)objectInputStream.readObject();
	    	List<Character> charactersInMatrix = new LinkedList<Character>();
	    	HashMap<String, Organ> organMap = new HashMap<String, Organ>();
	    	List<ColumnHead> columnHeads = rawMatrix.getColumnHeads();
	    	for(int i=1; i<columnHeads.size(); i++) {
	    		ColumnHead columnHead = columnHeads.get(i);
	    		
	    		String characterName = columnHead.getSource().getName();
	    		String connector = columnHead.getSource().getConnector();
	    		edu.arizona.biosemantics.matrixgeneration.model.complete.Character chara = columnHead.getSource();
	    		String organName = chara.getBearerStructureIdentifier().getDisplayName();
	    		
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
	    	}
	    	
	    	List<TaxonIdentification> taxonIdentifications = new LinkedList<TaxonIdentification>();
	    	Map<RankData, Taxon> rankTaxaMap = new HashMap<RankData, Taxon>();
	    	Map<Taxon, RowHead> taxonRowHeadMap = new HashMap<Taxon, RowHead>();
	    	for(RowHead rowHead : rawMatrix.getRowHeads()) {
	    		 TaxonIdentification taxonIdentification = 
	    				 rowHead.getSource().getTaxonIdentification();
	    		 taxonIdentifications.add(taxonIdentification);
	    		 String description = getMorphologyDescription(rowHead.getSource().getSourceFile());
	    		 Taxon taxon = createPlainTaxon(taxonIdentification, description);
	    		 rankTaxaMap.put(taxonIdentification.getRankData().getLast(), taxon);
	    		 taxonRowHeadMap.put(taxon, rowHead);
	    	 }
	    	
	    	for(TaxonIdentification taxonIdentification : taxonIdentifications) {
		    	LinkedList<RankData> rankData = taxonIdentification.getRankData();
		    	Taxon taxon = rankTaxaMap.get(rankData.getLast());
		    	if(rankData.size() == 1) 
		    		hierarchyTaxa.add(taxon);
			    if(rankData.size() > 1) {
			    	int parentRankIndex = rankData.size() - 2;
			    	Taxon parentTaxon = null;
			    	while(parentTaxon == null && parentRankIndex >= 0) {
				    	RankData parentRankData = rankData.get(parentRankIndex);
			    		parentTaxon = rankTaxaMap.get(parentRankData);
			    		parentRankIndex--;
			    	}
			    	if(parentTaxon == null)
			    		hierarchyTaxa.add(taxon);
			    	else
			    		parentTaxon.addChild(taxon);
			    }
		    }
	    	
		    TaxonMatrix taxonMatrix = new TaxonMatrix(hierarhicalCharacters, hierarchyTaxa);
		    
		    //set values
		    for(Taxon taxon : taxonMatrix.getFlatTaxa()) {
		    	RowHead rowHead = taxonRowHeadMap.get(taxon);
		    	for(int j=0; j<charactersInMatrix.size(); j++) {
		    		Character character = charactersInMatrix.get(j);
		    		String value = rawMatrix.getCellValues().get(rowHead).get(j + 1).getText();
		    		taxonMatrix.setValue(taxon, character, new Value(value));
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

	//TODO: Integrate rank authority and date
	private Taxon createPlainTaxon(TaxonIdentification taxonName, String description) {
		RankData lowestRank = taxonName.getRankData().getLast();
		return new Taxon(lowestRank.getRank(), lowestRank.getName(), taxonName.getAuthor(), taxonName.getDate(), description);
	}

	private String getCSVMatrix(TaxonMatrix matrix) throws IOException {
		//TODO: Add RankData authority and date
		try(StringWriter stringWriter = new StringWriter()) {
			try(CSVWriter writer = new CSVWriter(stringWriter)) {
			
				String[] line = new String[matrix.getCharacterCount() + 1];
				line[0] = "Name";
				
				for(int i=1; i<=matrix.getCharacterCount(); i++) {
					line[i] = matrix.getFlatCharacters().get(i - 1).toString();
				}
				writer.writeNext(line);
				
				for(Taxon taxon : matrix.getHierarchyTaxaDFS()) {
					line = new String[matrix.getCharacterCount() + 1];
					line[0] = getTaxonName(taxon);
					
					for(int j=1; j<matrix.getCharacterCount(); j++) {
						Character character = matrix.getFlatCharacters().get(j - 1);
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

	private String getTaxonName(Taxon taxon) {
		String name = "";
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

	private String getOutputFile(Task task) {
		return  Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.ser";
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
		//MatrixGenerationService service = new MatrixGenerationService();
		//service.createTaxonMatrix("C:/test/Test_mmm", "C:/test/Test_mmm.csv");
	}
}
