package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.common.taxonomy.TaxonIdentification;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands.ConsistencyCheck;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands.ExtraJvmInputVisualization;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands.ExtraJvmPossibleWorldsGeneration;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands.InJvmConsistencyCheck;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands.InputVisualization;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands.PossibleWorldsGeneration;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.HasTaskException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.PossibleWorldGenerationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;
import edu.arizona.biosemantics.euler.alignment.server.io.MatrixReviewModelReader;
import edu.arizona.biosemantics.euler.alignment.server.taxoncomparison.CharacterStateSimilarityMetric;
import edu.arizona.biosemantics.euler.alignment.server.taxoncomparison.RelationGenerator;
import edu.arizona.biosemantics.euler.alignment.server.taxoncomparison.lib.AllCombinationsArticulationsGenerator;
import edu.arizona.biosemantics.euler.alignment.server.taxoncomparison.lib.ContainmentCharacterStateSimilarityMetric;
import edu.arizona.biosemantics.euler.alignment.server.taxoncomparison.lib.TTestBasedRelationGenerator;
import edu.arizona.biosemantics.euler.alignment.shared.IEulerAlignmentService;
import edu.arizona.biosemantics.euler.alignment.shared.model.Articulation;
import edu.arizona.biosemantics.euler.alignment.shared.model.Articulation.Type;
import edu.arizona.biosemantics.euler.alignment.shared.model.Evidence;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;
import edu.arizona.biosemantics.euler.alignment.shared.model.PossibleWorld;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutputType;
import edu.arizona.biosemantics.euler.alignment.shared.model.Taxon;
import edu.arizona.biosemantics.euler.alignment.shared.model.Taxonomies;
import edu.arizona.biosemantics.euler.alignment.shared.model.Taxonomy;
import edu.arizona.biosemantics.euler.alignment.shared.model.taxoncomparison.ArticulationProposal;
import edu.arizona.biosemantics.euler.alignment.shared.model.taxoncomparison.RelationProposal;
import edu.arizona.biosemantics.euler.io.ArticulationsWriter;
import edu.arizona.biosemantics.euler.io.EulerInputFileWriter;
import edu.arizona.biosemantics.euler.io.TaxonomyFileReader;

public class TaxonomyComparisonService extends RemoteServiceServlet implements ITaxonomyComparisonService {
	
	private String tempFiles = Configuration.taxonomyComparison_tempFileBase;
	private IFileService fileService;
	private IEulerAlignmentService alignmentService;
	//private IFileFormatService fileFormatService = new FileFormatService();
	//private IFileAccessService fileAccessService = new FileAccessService();
	private IFilePermissionService filePermissionService;
	private ListeningScheduledExecutorService executorService;
	private Map<Integer, ListenableFuture<Void>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Void>>();
	private Map<Integer, PossibleWorldsGeneration> activeProcess = new HashMap<Integer, PossibleWorldsGeneration>();
	private DAOManager daoManager;
	private Emailer emailer;
	private InputFileCreator inputFileCreator;
	
	@Inject
	public TaxonomyComparisonService(IFileService fileService, IFilePermissionService filePermissionService, 
			DAOManager daoManager, Emailer emailer, IEulerAlignmentService alignmentService, InputFileCreator inputFileCreator) {
		this.fileService = fileService;
		this.filePermissionService = filePermissionService;
		this.daoManager = daoManager;
		this.emailer = emailer;
		this.inputFileCreator = inputFileCreator;
		this.alignmentService = alignmentService;
		executorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(Configuration.maxActiveTaxonomyComparison));
		File taxonomyComparisonCache = new File(Configuration.taxonomyComparison_tempFileBase);
		if(!taxonomyComparisonCache.exists())
			taxonomyComparisonCache.mkdirs();			
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
		
	private Model createModelFromCleanTaxInput(AuthenticationToken token, Task task) throws TaxonomyComparisonException {
		TaxonomyComparisonConfiguration config = this.getTaxonomyComparisonConfiguration(task);
		String input = config.getCleanTaxInput();
		File dir = new File(input);
		Taxonomies taxonomies = new Taxonomies();
		for(File file : dir.listFiles()) {
			TaxonomyFileReader reader = new TaxonomyFileReader(file.getAbsolutePath());
			Taxonomy taxonomy;
			try {
				taxonomy = reader.read();
			} catch (Exception e) {
				log(LogLevel.ERROR, "Couldn't read taxonomy in file " + file.getAbsolutePath(), e);
				throw new TaxonomyComparisonException();
			}
			taxonomies.add(taxonomy);
		}
		Model model = new Model(taxonomies, null);
		return model;
	}
	
	@Override
	public edu.arizona.biosemantics.euler.alignment.shared.model.Collection getCollection(AuthenticationToken token, Task task) throws TaxonomyComparisonException {
		TaxonomyComparisonConfiguration config = this.getTaxonomyComparisonConfiguration(task);
		try {
			edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection = 
					alignmentService.getCollection(config.getAlignmentId(), config.getAlignmentSecret());
			return collection;
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get collection", e);
			throw new TaxonomyComparisonException(task);
		}
	}
	
	@Override 
	public void saveCollection(AuthenticationToken token, Task task, edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection) throws TaxonomyComparisonException {
		try {
			alignmentService.saveCollection(collection);
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't serialize model to disk", e);
			throw new TaxonomyComparisonException(task);
		}
	}
	
	//Assumption: Does not take long, hence blocking
	//TODO: can't be blocking. Has to also show processing Dialog, which means it needs to be a resumable task later
	@Override
	public boolean isConsistentInput(AuthenticationToken token, Task task, 
			final edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection) throws TaxonomyComparisonException {
		return true;
		/*final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
		final Model model = collection.getModel();
				
		final String eulerInputFile = tempFiles + File.separator + task.getId() + File.separator + "input.txt";
		try {
			writeEulerInput(eulerInputFile, model);
		} catch(IOException e) {
			log(LogLevel.ERROR, "Couldn't write euler input to file " , e);
		}
		
		String runId = String.valueOf(model.getRunHistory().size());
		final String workingDir = tempFiles + File.separator + task.getId() + File.separator + "run" + File.separator + runId;
		final String outputDir = workingDir + File.separator + "out";
		try {
			fileService.deleteFile(new AdminAuthenticationToken(), workingDir);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't delete working directory", e);
			throw new TaxonomyComparisonException(task);
		}
		try {
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId() + File.separator + "run", runId, false);
			fileService.createDirectory(new AdminAuthenticationToken(), workingDir, "out", false);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't set up output directory", e);
			throw new TaxonomyComparisonException(task);
		}
		
		final ConsistencyCheck consistencyCheck = new InJvmConsistencyCheck(eulerInputFile, workingDir, outputDir);
		try {
			Boolean result = consistencyCheck.call();
			return result;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't run consistency check", e);
			throw new TaxonomyComparisonException(task);
		}*/
	}
	
	@Override
	public void stopPossibleWorldGeneration(final AuthenticationToken token, final Task task) throws TaxonomyComparisonException {
		TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
		log(LogLevel.INFO, "Stop possible world generation for config: " + config.getConfiguration().getId());
		
		//order matters: First destroy active process, then cancel future. If future canceled first future result listener (that handles successful completion fires)
		if(activeProcessFutures.containsKey(config.getConfiguration().getId())) {
			log(LogLevel.INFO, "Cancel active process future for config: " + config.getConfiguration().getId());
			ListenableFuture<Void> processFuture = this.activeProcessFutures.get(config.getConfiguration().getId());
			processFuture.cancel(true);
		}
	}
	
	@Override
	public Task runPossibleWorldGeneration(final AuthenticationToken authenticationToken, final Task task, 
			final edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection) throws TaxonomyComparisonException {
		final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
		final Model model = collection.getModel();
		
		if(activeProcessFutures.containsKey(config.getConfiguration().getId())) {
			return task;
		} else {
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(TaskTypeEnum.TAXONOMY_COMPARISON);
			TaskStage taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.ANALYZE.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			task.setTooLong(false);
			daoManager.getTaskDAO().updateTask(task);
			
			final String eulerInputFile = tempFiles + File.separator + task.getId() + File.separator + "input.txt";
			try {
				writeEulerInput(eulerInputFile, model);
			} catch(IOException e) {
				log(LogLevel.ERROR, "Couldn't write euler input to file " , e);
			}
			
			String runId = String.valueOf(model.getRunHistory().size());
			final String workingDir = tempFiles + File.separator + task.getId() + File.separator + "run" + File.separator + runId;
			final String outputDir = workingDir + File.separator + "out";
			try {
				fileService.deleteFile(new AdminAuthenticationToken(), workingDir);
			} catch(Exception e) {
				log(LogLevel.ERROR, "Couldn't delete output directory", e);
				throw new TaxonomyComparisonException(task);
			}
			try {
				fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId() + File.separator + "run", runId, false);
				fileService.createDirectory(new AdminAuthenticationToken(), workingDir, "out", false);
			} catch(Exception e) {
				log(LogLevel.ERROR, "Couldn't set up output directory", e);
				throw new TaxonomyComparisonException(task);
			}

			final PossibleWorldsGeneration possibleWorldGeneration = new ExtraJvmPossibleWorldsGeneration(eulerInputFile, outputDir, workingDir);
			//final PossibleWorldsGeneration possibleWorldGeneration = new InJvmPossibleWorldsGeneration(eulerInputFile, outputDir, workingDir);
			//final PossibleWorldsGeneration possibleWorldGeneration = new DummyPossibleWorldsGeneration(eulerInputFile, outputDir);

 			log(LogLevel.INFO, "active processes: " + activeProcess);
			activeProcess.put(config.getConfiguration().getId(), possibleWorldGeneration);
 			log(LogLevel.INFO, "active processes: " + activeProcess);
			final ListenableFuture<Void> futureResult = executorService.submit(possibleWorldGeneration);
			this.activeProcessFutures.put(config.getConfiguration().getId(), futureResult);
			executorService.schedule(new Runnable() {
				public void run() {
					try {
						stopPossibleWorldGeneration(authenticationToken, task);
						log(LogLevel.ERROR, "MIR Generation took too long and was canceled.");
					} catch(TaxonomyComparisonException e) {

						log(LogLevel.ERROR, "MIR Generation could not be canceled.", e);
					}
				}
			}, Configuration.taxonomyComparisonTask_maxRunningTimeMinutes, TimeUnit.MINUTES);
			futureResult.addListener(new Runnable() {
			    	public void run() {	
			     		try {
			     			log(LogLevel.INFO, config.getConfiguration().getId() + " completed running");
			     			PossibleWorldsGeneration possibleWorldsGeneration = activeProcess.remove(config.getConfiguration().getId());
				    		if(possibleWorldsGeneration instanceof ExtraJvmCallable<?>) {
				    			ExtraJvmCallable<?> extraJvmCallable = (ExtraJvmCallable<?>)possibleWorldsGeneration;
				    			log(LogLevel.INFO, "info: " + extraJvmCallable.getStdOut());
				    			if(!extraJvmCallable.getErr().isEmpty())
					    			log(LogLevel.ERROR, "error: " + extraJvmCallable.getErr());
				    		}
			     			
			     			ListenableFuture<Void> futureResult = activeProcessFutures.remove(config.getConfiguration().getId());
				     		if(possibleWorldsGeneration != null && futureResult != null) {
					    		if(futureResult.isCancelled()) {
					    			log(LogLevel.INFO, config.getConfiguration().getId() + " was canceled");
				     				task.setTooLong(futureResult.isCancelled());
				     				daoManager.getTaskDAO().updateTask(task);
					    			log(LogLevel.INFO, config.getConfiguration().getId() + " will be destroyed");
					    			possibleWorldsGeneration.destroy();
					    		} else {
					     			if(possibleWorldsGeneration.isExecutedSuccessfully()) {
						     			if(!futureResult.isCancelled()) {
											TaskStage newTaskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.ANALYZE_COMPLETE.toString());
											task.setTaskStage(newTaskStage);
											task.setResumable(true);
											daoManager.getTaskDAO().updateTask(task);
											
											// send an email to the user who owns the task.
											sendFinishedTaxonomyComparisonEmail(task);
						     			}
					     			
						     			save(authenticationToken, task, collection);
					     			}
					    		}
				     		}
			     		} catch(Throwable t) {
			     			log(LogLevel.ERROR, t.getMessage()+"\n"+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
			     			/*task.setFailed(true);
							task.setFailedTime(new Date());
							
							daoManager.getTaskDAO().updateTask(task);*/
			     		}
			     	}

					
			     }, executorService);
			
			return task;
		}
	}
	
	protected void save(AuthenticationToken token, Task task, 
			edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection) throws TaxonomyComparisonException {
		try {
			Model model = collection.getModel();
			RunOutput output = this.getRunOutput(task);
			for(PossibleWorld possibleWorld : output.getPossibleWorlds()) {
				String oldUrl = possibleWorld.getUrl();
				possibleWorld.setUrl(getAuthenticatedGetPDFUrl(token, possibleWorld.getUrl()));
			}
			output = new RunOutput(output.getType(), output.getPossibleWorlds(), getAuthenticatedGetPDFUrl(token, output.getAggregateUrl()), 
					getAuthenticatedGetPDFUrl(token, output.getDiagnosisUrl()));
			
			if(!model.getRunHistory().isEmpty())
				model.getRunHistory().getLast().setOutput(output);
			
			this.saveCollection(token, task, collection);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't save model", e);
			throw new TaxonomyComparisonException();
		}
	}
	
	private String getAuthenticatedGetPDFUrl(AuthenticationToken token, String url) throws UnsupportedEncodingException {
		return "result.gpdf?target=" + URLEncoder.encode(url, "UTF-8")
				+ "&userID=" + URLEncoder.encode(String.valueOf(token.getUserId()), "UTF-8") + "&"
				+ "sessionID=" + URLEncoder.encode(token.getSessionID(), "UTF-8");
	}

	private void writeEulerInput(String eulerInputFile, Model model) throws IOException {
		EulerInputFileWriter eulerInputFileWriter = new EulerInputFileWriter(eulerInputFile);
		eulerInputFileWriter.write(model);
	}

	private void sendFinishedTaxonomyComparisonEmail(Task task) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getInputVisualization(AuthenticationToken token, Task task, 
			edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection) throws TaxonomyComparisonException {
		Model model = collection.getModel();
		final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
		final String eulerInputFile = tempFiles + File.separator + task.getId() + File.separator + "input.txt";
		try {
			writeEulerInput(eulerInputFile, model);
		} catch(IOException e) {
			log(LogLevel.ERROR, "Couldn't write euler input to file " , e);
		}
		
		final String workingDir = tempFiles + File.separator + task.getId() + File.separator + "inputVisualization";
		final String outputDir = workingDir + File.separator + "out";
		try {
			fileService.deleteFile(new AdminAuthenticationToken(), workingDir);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't delete output directory", e);
			throw new TaxonomyComparisonException(task);
		}
		try {
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId(), "inputVisualization", false);
			fileService.createDirectory(new AdminAuthenticationToken(), workingDir, "out", false);
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't set up output directory", e);
			throw new TaxonomyComparisonException(task);
		}

		InputVisualization inputVisualization = new ExtraJvmInputVisualization(eulerInputFile, outputDir, workingDir);
		//InputVisualization inputVisualization = new InJvmInputVisualization(eulerInputFile, outputDir, workingDir);
		//InputVisualization inputVisualization = new DummyInputVisualization(eulerInputFile, outputDir);
		try {
			inputVisualization.call();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't run input visualization", e);
			throw new TaxonomyComparisonException(task);
		}
		File output = new File(tempFiles + File.separator + task.getId() + File.separator + "inputVisualization" + File.separator + "out" + File.separator + "0-Input" + File.separator + "input.pdf");
		return output.getAbsolutePath();
	}

	
	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.TAXONOMY_COMPARISON)) {
						return task;
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws TaxonomyComparisonException {
		task = daoManager.getTaskDAO().getTask(task.getId());
		final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
						
		//remove taxonomy comparison configuration
		if(config.hasCleanTaxInput())
			try {
				fileService.setInUse(authenticationToken, false, config.getCleanTaxInput(), task);
			} catch (PermissionDeniedException e) {
				throw new TaxonomyComparisonException(task);
			}
		if(config.hasModelInputs()) 
			try {
				fileService.setInUse(authenticationToken, false, config.getModelInput1(), task);
				fileService.setInUse(authenticationToken, false, config.getModelInput2(), task);
			} catch (PermissionDeniedException e) {
				throw new TaxonomyComparisonException(task);
			}
		daoManager.getTaxonomyComparisonConfigurationDAO().remove(config);
		
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
			case ALIGN:
				break;
			case ANALYZE:
				stopPossibleWorldGeneration(authenticationToken, task);
				break; 
			case ANALYZE_COMPLETE:
				break;
			default:
				break;
			}
		}
	}

	private TaxonomyComparisonConfiguration getTaxonomyComparisonConfiguration(Task task) throws TaxonomyComparisonException {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof TaxonomyComparisonConfiguration))
			throw new TaxonomyComparisonException(task);
		final TaxonomyComparisonConfiguration taxonomyComparisonConfiguration = (TaxonomyComparisonConfiguration)configuration;
		return taxonomyComparisonConfiguration;
	}

	@Override
	public PossibleWorldGenerationResult getPossibleWorldGenerationResult(AuthenticationToken token, Task task) {
		//final AbstractTaskConfiguration configuration = task.getConfiguration();
		task = daoManager.getTaskDAO().getTask(task.getId());
		if(task.isTooLong()) 
			return new PossibleWorldGenerationResult(true, null);
		TaskStage newTaskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.ALIGN.toString());
		task.setTaskStage(newTaskStage);
		task.setResumable(true);
		daoManager.getTaskDAO().updateTask(task);
		
		return new PossibleWorldGenerationResult(false, getRunOutput(task));
		
		//temporary for dummy use
		//if(Math.random() < 0.5) {
		//	return new RunOutput(RunOutputType.CONFLICT, possibleWorldFiles, "", diagnosisUrl); 
		//}
		//return new RunOutput(RunOutputType.MULTIPLE, possibleWorldFiles, aggregateUrl, diagnosisUrl);
	}
	


	private RunOutput getRunOutput(Task task) {
		File runFile = new File(tempFiles + File.separator + task.getId() + File.separator + "run");
		int runs = runFile.listFiles().length;
		File possibleWorldDir = new File(tempFiles + File.separator + task.getId() + File.separator + "run" + File.separator + String.valueOf(runs) + 
				File.separator + "out" + File.separator + "4-PWs");	
		String aggregateUrl = tempFiles + File.separator + task.getId() + File.separator + "run" + File.separator + String.valueOf(runs) + 
				File.separator + "out" + File.separator + "5-Aggregates" + File.separator + "input_regular_all.pdf";
				
		List<PossibleWorld> possibleWorldFiles = new LinkedList<PossibleWorld>();
		for(File file : possibleWorldDir.listFiles()) 
			if(file.isFile() && file.getName().endsWith(".pdf")) 
				possibleWorldFiles.add(new PossibleWorld(file.getAbsolutePath(), file.getName()));
		
		String diagnosisUrl = "diagnosis"; // This a placeholder for when euler2 produces this again.
		
		//TODO subclass RunOutput for conflict vs. pw available scenario; use instead of type
		if(possibleWorldFiles.size() == 1)
			return new RunOutput(RunOutputType.ONE, possibleWorldFiles, aggregateUrl, diagnosisUrl);
		if(possibleWorldFiles.size() > 1)
			return new RunOutput(RunOutputType.MULTIPLE, possibleWorldFiles, aggregateUrl, diagnosisUrl);
		return new RunOutput(RunOutputType.CONFLICT, possibleWorldFiles, "", diagnosisUrl); 
	}

	@Override
	public Task goToTaskStage(AuthenticationToken token, Task task, TaskStageEnum taskStageEnum) {
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(TaskTypeEnum.TAXONOMY_COMPARISON);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		TaxonomyComparisonException exc = new TaxonomyComparisonException();
		try(ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File("test"))))) {
			output.writeObject(exc);
		}
	}

	@Override
	public String exportArticulations(AuthenticationToken token, Task task,
			edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection) throws TaxonomyComparisonException {
		Model model = collection.getModel();
		final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
		
		String path = tempFiles + File.separator + String.valueOf(task.getId()) + File.separator + "articulations_task-" + task.getName() + ".txt";
		File file = new File(path);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't create output file", e);
			throw new TaxonomyComparisonException(task);
		}
		ArticulationsWriter writer = new ArticulationsWriter();
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
			bw.write(writer.write(model));
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage()+"\n"+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
			throw new TaxonomyComparisonException(task);
		} 
		return path;
	}
	
	@Override
	public List<Task> getResumables(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getResumableTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.TAXONOMY_COMPARISON)) {
				result.add(task);
			}
		}
		return result;
	}
	
	@Override
	public List<String> getTaxonomies(AuthenticationToken token, FolderTreeItem folder) throws Exception {
		List<String> result = new LinkedList<String>();
		List<FileTreeItem> filesCleanTax = fileService.getFiles(token, folder, FileFilter.CLEANTAX);
		for(FileTreeItem fileTreeItem : filesCleanTax) {
			result.add(getRootNameFromCleantax(new File(fileTreeItem.getFilePath())));
		}
		List<FileTreeItem> filesMatrixGeneration = fileService.getFiles(token, folder, FileFilter.MATRIX_GENERATION_SERIALIZED_MODEL);
		for(FileTreeItem fileTreeItem : filesMatrixGeneration) {
			result.add(getRootNameFromMatrixGenerationModel(new File(fileTreeItem.getFilePath())));
		}
		return result;
	}

	private String getRootNameFromMatrixGenerationModel(File file) throws FileNotFoundException, ClassNotFoundException, IOException {
		edu.arizona.biosemantics.matrixreview.shared.model.Model model = unserializeModel(file);
		return model.getTaxonMatrix().getHierarchyRootTaxa().get(0).getName();
	}
	
	private edu.arizona.biosemantics.matrixreview.shared.model.Model unserializeModel(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			edu.arizona.biosemantics.matrixreview.shared.model.Model model = (edu.arizona.biosemantics.matrixreview.shared.model.Model)input.readObject();
			return model;
		}
	}

	private String getRootNameFromCleantax(File file) throws Exception {
		CleanTaxReader reader = new CleanTaxReader();
		return reader.getRootName(file);
	}
		
	private String getSerializedModel(String input) {
		File dir = new File(input);
		for(File child : dir.listFiles()) {
			if(child.getName().endsWith(".ser"))
				return child.getAbsolutePath();
		}
		return null;
	}

	private edu.arizona.biosemantics.matrixreview.shared.model.Model unserializeMatrix(String file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			edu.arizona.biosemantics.matrixreview.shared.model.Model model = 
					(edu.arizona.biosemantics.matrixreview.shared.model.Model)input.readObject();
			return model;
		}
	}
	
	@Override
	public Task startFromCleantax(AuthenticationToken token, String taskName, String input, String taxonGroup, String ontology, String termReview1, String termReview2) throws HasTaskException {
		String[] inputs = inputFileCreator.createOwnedIfShared(token, new String[] {input, ontology, termReview1, termReview2});
		
		TaxonomyComparisonConfiguration config = new TaxonomyComparisonConfiguration();
		config.setCleanTaxInput(inputs[0]);
		TaxonGroup group = daoManager.getTaxonGroupDAO().getTaxonGroup(taxonGroup);
		config.setTaxonGroup(group);
		config.setOntology(inputs[1]);
		config.setTermReview1(inputs[2]); 
		config.setTermReview2(inputs[3]);
		config.setOutput(config.getModelInput1() + "_output_by_TaxC_task_" + taskName);
		
		config.setOutput(config.getCleanTaxInput() + "_output_by_TaxC_task_" + taskName);
		config = daoManager.getTaxonomyComparisonConfigurationDAO().addTaxonomyComparisonConfiguration(config);
		
		TaskTypeEnum taskType =TaskTypeEnum.TAXONOMY_COMPARISON;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.INPUT.toString());
		TinyUser user = daoManager.getUserDAO().getTinyUser(token.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);
		
		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.ALIGN.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		
		try {
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles, String.valueOf(task.getId()), false);
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId(), "run", false);
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId(), "inputVisualization", false);
		} catch(PermissionDeniedException | CreateDirectoryFailedException e) {
			log(LogLevel.ERROR, "can not create cache directory.");
			throw new TaxonomyComparisonException(task);
		}
		Model model = createModelFromCleanTaxInput(token, task);
		
		inputFileCreator.setInUse(token, new String[] { input, termReview1, termReview2, ontology }, task);
		
		edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection = new edu.arizona.biosemantics.euler.alignment.shared.model.Collection();
		collection.setSecret(String.valueOf(task.getId()));
		collection.setModel(model);
		collection.setGlossaryPath1(config.getTermReview1());
		collection.setGlossaryPath2(config.getTermReview2());
		collection.setTaxonGroup(edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup));
		collection.setOntologyPath(config.getOntology());
		try {
			collection = alignmentService.createCollection(collection);
			config.setAlignmentId(collection.getId());
			config.setAlignmentSecret(collection.getSecret());
			daoManager.getTaxonomyComparisonConfigurationDAO().updateTaxonomyComparisonQueryConfiguration(config);
		} catch (Exception e) {
			e.printStackTrace();
			log(LogLevel.ERROR, "Could not create collection", e);
			throw new TaxonomyComparisonException(task);
		}
		return task;
	}

	@Override
	public Task startFromSerializedModels(AuthenticationToken token, String taskName, String modelInput1, String modelInput2, 
			String modelAuthor1, String modelYear1, String modelAuthor2, String modelYear2,
			String taxonGroup, String ontology, String termReview1, String termReview2) throws HasTaskException {
		String[] inputs = inputFileCreator.createOwnedIfShared(token, new String[] {modelInput1, modelInput2, ontology, termReview1, termReview2});
		
		TaxonomyComparisonConfiguration config = new TaxonomyComparisonConfiguration();
		config.setModelInput1(inputs[0]);
		config.setModelInput2(inputs[1]);
		config.setModel1Author(modelAuthor1);
		config.setModel2Author(modelAuthor2);
		config.setModel1Year(modelYear1);
		config.setModel2Year(modelYear2);
		TaxonGroup group = daoManager.getTaxonGroupDAO().getTaxonGroup(taxonGroup);
		config.setTaxonGroup(group);
		config.setOntology(inputs[2]);
		config.setTermReview1(inputs[3]); 
		config.setTermReview2(inputs[4]);
		config.setOutput(config.getModelInput1() + "_output_by_TaxC_task_" + taskName);
		config = daoManager.getTaxonomyComparisonConfigurationDAO().addTaxonomyComparisonConfiguration(config);
		
		TaskTypeEnum taskType = TaskTypeEnum.TAXONOMY_COMPARISON;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.INPUT.toString());
		TinyUser user = daoManager.getUserDAO().getTinyUser(token.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);
		
		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getTaxonomyComparisonTaskStage(TaskStageEnum.ALIGN.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		
		try {
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles, String.valueOf(task.getId()), false);
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId(), "run", false);
			fileService.createDirectory(new AdminAuthenticationToken(), tempFiles + File.separator + task.getId(), "inputVisualization", false);
		} catch(PermissionDeniedException | CreateDirectoryFailedException e) {
			log(LogLevel.ERROR, "can not create cache directory.");
			throw new TaxonomyComparisonException(task);
		}
		Model model = createModelFromInputModels(token, task);
		
		inputFileCreator.setInUse(token, new String[] { modelInput1, modelInput2, termReview1, termReview2, ontology }, task);
		
		edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection = new edu.arizona.biosemantics.euler.alignment.shared.model.Collection();
		collection.setSecret(String.valueOf(task.getId()));
		collection.setModel(model);
		collection.setGlossaryPath1(config.getTermReview1());
		collection.setGlossaryPath2(config.getTermReview2());
		collection.setTaxonGroup(edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup));
		if(config.getOntology() != null) {
			File ontologyDir = new File(config.getOntology());
			if(ontologyDir.exists() && ontologyDir.isDirectory()) {
				File ontologyFile = null;
				for(File child : ontologyDir.listFiles()) {
					if(child.getName().endsWith(".owl") && !child.getName().startsWith("module.")) {
						ontologyFile = child;
					}
				}
				if(ontologyFile != null) {
					collection.setOntologyPath(ontologyFile.getAbsolutePath());
				} else {
					throw new TaxonomyComparisonException();
				}
			} else {
				throw new TaxonomyComparisonException();
			}
		}
		try {
			collection = alignmentService.createCollection(collection);
			config.setAlignmentId(collection.getId());
			config.setAlignmentSecret(collection.getSecret());
			daoManager.getTaxonomyComparisonConfigurationDAO().updateTaxonomyComparisonQueryConfiguration(config);
		} catch (Exception e) {
			e.printStackTrace();
			log(LogLevel.ERROR, "Could not create collection", e);
			throw new TaxonomyComparisonException(task);
		}
		return task;
	}

	private Model createModelFromInputModels(AuthenticationToken token, Task task) throws TaxonomyComparisonException {
		TaxonomyComparisonConfiguration config = this.getTaxonomyComparisonConfiguration(task);
		String modelInput1 = config.getModelInput1();
		String modelInput2 = config.getModelInput2();
		Taxonomies taxonomies = new Taxonomies();
		try {
			edu.arizona.biosemantics.matrixreview.shared.model.Model reviewModel1 = this.unserializeMatrix(getSerializedModel(modelInput1));
			edu.arizona.biosemantics.matrixreview.shared.model.Model reviewModel2 = this.unserializeMatrix(getSerializedModel(modelInput2));
			Taxonomy taxonomy1 = createTaxonomy(reviewModel1);
			Taxonomy taxonomy2 = createTaxonomy(reviewModel2);
			//euler / the reasoner does not work with a lot of symbols in the taxonomy id
			// can't use _ @ as separators
			taxonomy1.setId(config.getModel1Author() + "" + config.getModel1Year());
			taxonomy2.setId(config.getModel2Author() + "" + config.getModel2Year());
			taxonomy1.setYear(config.getModel1Year());
			taxonomy2.setYear(config.getModel2Year());
			taxonomy1.setAuthor(config.getModel1Author());
			taxonomy2.setAuthor(config.getModel2Author());
			taxonomies.add(taxonomy1);
			taxonomies.add(taxonomy2);
			
			Map<Taxon, Map<Taxon, List<Evidence>>> evidenceMap = new HashMap<Taxon, Map<Taxon, List<Evidence>>>();
			try {
				Collection<ArticulationProposal> proposals = this.getMachineArticulationProposals(token, task, taxonomies.get(0), taxonomies.get(1));
				
				Map<TaxonIdentification, Taxon> identificationTaxonMap1 = new HashMap<TaxonIdentification, Taxon>();
				Map<TaxonIdentification, Taxon> identificationTaxonMap2 = new HashMap<TaxonIdentification, Taxon>();
				for(Taxon taxon : taxonomies.get(0).getTaxaDFS()) {
					identificationTaxonMap1.put(taxon.getTaxonIdentification(), taxon);
				}
				for(Taxon taxon : taxonomies.get(1).getTaxaDFS()) {
					identificationTaxonMap2.put(taxon.getTaxonIdentification(), taxon);
				}
				
				for(ArticulationProposal proposal : proposals) {
					Taxon taxonA = identificationTaxonMap1.get(proposal.getCharacterizedTaxonA().getTaxonIdentification());
					Taxon taxonB = identificationTaxonMap2.get(proposal.getCharacterizedTaxonB().getTaxonIdentification());
					if(taxonA != null && taxonB != null) {
						if(!evidenceMap.containsKey(taxonA))
							evidenceMap.put(taxonA, new HashMap<Taxon, List<Evidence>>());
						evidenceMap.get(taxonA).put(taxonB, proposal.getEvidence());
					}
				}
			} catch (Exception e) {
				log(LogLevel.ERROR, "Couldn't get machine articulations.", e);
				throw new TaxonomyComparisonException();
			}
			Model model = new Model(taxonomies, evidenceMap);
			return model;
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.ERROR, "Could not unserialize review model 2.", e);
			throw new TaxonomyComparisonException();
		}
	}

	private Taxonomy createTaxonomy(edu.arizona.biosemantics.matrixreview.shared.model.Model reviewModel) throws TaxonomyComparisonException {
		MatrixReviewModelReader matrixReviewModelReader = new MatrixReviewModelReader();
		try {
			Taxonomy taxonomy = matrixReviewModelReader.getTaxonomy(reviewModel);
			return taxonomy;
		} catch(Exception e) {
			throw new TaxonomyComparisonException(e.getMessage());
		}
	}

	@Override
	public Boolean isValidCleanTaxInput(AuthenticationToken token, String inputFile) {
		File file = new File(inputFile);
		if(!file.isDirectory())
			return false;
		int count = file.listFiles().length;
		if(count != 2) 
			return false;
		return true;
	}

	@Override
	public Boolean isValidModelInput(AuthenticationToken token, String inputFile) {
		File file = new File(inputFile);
		if(!file.isDirectory())
			return false;
		/*int count = file.listFiles().length;
		if(count != 2) 
			return false;*/
		return true;
	}
	
	@Override
	public List<Articulation> getMachineArticulations(AuthenticationToken token, Task task, 
			edu.arizona.biosemantics.euler.alignment.shared.model.Collection collection, double threshold) throws Exception {
		//TODO
		List<Articulation> result = new LinkedList<Articulation>();
		/*Collection<ArticulationProposal> proposals = this.getMachineArticulationProposals(token, task, model.getTaxonomies().get(0), 
				model.getTaxonomies().get(1)); 
		
		//cleantax format doesnt allow for full taxonIdentification information. Thus string-based comparison so far.
		Map<TaxonIdentification, Taxon> identificationTaxonMap1 = new HashMap<TaxonIdentification, Taxon>();
		Map<TaxonIdentification, Taxon> identificationTaxonMap2 = new HashMap<TaxonIdentification, Taxon>();
		for(Taxon taxon : model.getTaxonomies().get(0).getTaxaDFS()) {
			identificationTaxonMap1.put(taxon.getTaxonIdentification(), taxon);
		}
		for(Taxon taxon :  model.getTaxonomies().get(1).getTaxaDFS()) {
			identificationTaxonMap2.put(taxon.getTaxonIdentification(), taxon);
		}
		
		//proposals.iterator().next().getRelationProposals().iterator().next().getRelation();
		for(ArticulationProposal proposal : proposals)
			for(RelationProposal relationProposal : proposal.getRelationProposals()) {
				if(relationProposal.getConfidence() > threshold) {
					Taxon characterizedTaxonA = proposal.getCharacterizedTaxonA();
					Taxon taxonA = identificationTaxonMap1.get(characterizedTaxonA.getTaxonIdentification());
					Taxon characterizedTaxonB = proposal.getCharacterizedTaxonB();
					Taxon taxonB = identificationTaxonMap2.get(characterizedTaxonB.getTaxonIdentification());
					if(taxonA != null && taxonB != null) 				
						result.add(new Articulation(taxonA, taxonB, relationProposal.getRelation(), Type.MACHINE));
				}
			}*/
		return result;
	}

	private Collection<ArticulationProposal> getMachineArticulationProposals(AuthenticationToken token, Task task, Taxonomy taxonomy1, Taxonomy taxonomy2)
			throws Exception {
		List<ArticulationProposal> result = new LinkedList<ArticulationProposal>();
		/*final TaxonomyComparisonConfiguration config = getTaxonomyComparisonConfiguration(task);
		if(config.hasModelInputs()) {
			edu.arizona.biosemantics.matrixreview.shared.model.Model model1 = unserializeMatrix(getSerializedModel(config.getModelInput1()));
			edu.arizona.biosemantics.matrixreview.shared.model.Model model2 = unserializeMatrix(getSerializedModel(config.getModelInput1()));
			
			MatrixReviewModelReader matrixReviewModelReader = new MatrixReviewModelReader();
			Taxonomy taxonomyA = matrixReviewModelReader.getTaxonomy(model1);
			Taxonomy taxonomyB = matrixReviewModelReader.getTaxonomy(model2);
			
			CharacterStateSimilarityMetric characterStateSimilarityMetric = new ContainmentCharacterStateSimilarityMetric();
			RelationGenerator pairwiseArticulationGenerator = new TTestBasedRelationGenerator(
					edu.arizona.biosemantics.taxoncomparison.comparison.Configuration.disjointSimilarityMax, 
					edu.arizona.biosemantics.taxoncomparison.comparison.Configuration.symmetricDifferenceMax, 
					edu.arizona.biosemantics.taxoncomparison.comparison.Configuration.congurenceSimilarityMin, 
					edu.arizona.biosemantics.taxoncomparison.comparison.Configuration.inclusionSimilarityMin, 
					edu.arizona.biosemantics.taxoncomparison.comparison.Configuration.asymmetricDifferenceMin, 
					characterStateSimilarityMetric, taxonomyA, taxonomyB);
			AllCombinationsArticulationsGenerator allCombinationsArticulationsGenerator = 
					new AllCombinationsArticulationsGenerator(pairwiseArticulationGenerator);	
			Collection<ArticulationProposal> proposals = allCombinationsArticulationsGenerator.generate(taxonomyA, taxonomyB);		
			return proposals;
		}*/
		return result;
	}
	
}
