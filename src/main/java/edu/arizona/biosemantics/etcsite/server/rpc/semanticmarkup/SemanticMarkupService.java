package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
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
import edu.arizona.biosemantics.etcsite.server.Zipper;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGeneration;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Glossary;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.process.semanticmarkup.BracketValidator;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
/*import edu.arizona.biosemantics.etcsite.shared.db.otolite.OrderCategoriesDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.StructuresDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.SynonymsDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.TermCategoryPairDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.TermsInOrderCategoryDAO;*/
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.FileDeleteFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.SetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;
import edu.arizona.biosemantics.oto2.oto.server.db.CollectionDAO;
import edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;

public class SemanticMarkupService extends RemoteServiceServlet implements ISemanticMarkupService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private IFileFormatService fileFormatService = new FileFormatService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private IUserService userService = new UserService();
	private BracketValidator bracketValidator = new BracketValidator();
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<LearnResult>> activeLearnFutures = new HashMap<Integer, ListenableFuture<LearnResult>>();
	private Map<Integer, ListenableFuture<ParseResult>> activeParseFutures = new HashMap<Integer, ListenableFuture<ParseResult>>();
	private Map<Integer, Learn> activeLearns = new HashMap<Integer, Learn>();
	private Map<Integer, Parse> activeParses = new HashMap<Integer, Parse>();
	private DAOManager daoManager = new DAOManager();
	private Emailer emailer = new Emailer();
	private ICollectionService otoCollectionService = new CollectionService();
	
	public SemanticMarkupService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Configuration.maxActiveSemanticMarkup));
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Task start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName) throws SemanticMarkupException {
		boolean isShared = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), filePath);
		String fileNameResult;
		try {
			fileNameResult = fileService.getFileName(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		if(isShared) {
			String destinationResult;
			try {
				destinationResult = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
						fileNameResult, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new SemanticMarkupException();
			}
			try {
				fileService.copyFiles(authenticationToken, filePath, destinationResult);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new SemanticMarkupException();
			}
			filePath = destinationResult;
		}
		
		List<String> directoriesFilesResult;
		try {
			directoriesFilesResult = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		int numberOfInputFiles = directoriesFilesResult.size();
		Glossary glossary = daoManager.getGlossaryDAO().getGlossary(glossaryName);
		SemanticMarkupConfiguration config = new SemanticMarkupConfiguration();
		config.setInput(filePath);	
		config.setGlossary(glossary);
		config.setNumberOfInputFiles(numberOfInputFiles);
		config.setOutput(config.getInput() + "_output_by_TC_task_" + taskName);
		config = daoManager.getSemanticMarkupConfigurationDAO().addSemanticMarkupConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.INPUT.toString());
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);

		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
		List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
		if(result.isEmpty())
			taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		
		try {
			fileService.setInUse(authenticationToken, true, filePath, task);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		return task;
	}

	@Override
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, Task task) {	
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		//do preprocessing here, return result immediately or always only return an invocation
		//and make user come back when ready?
		
		List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
		return result;
	}
	
	@Override
	public LearnInvocation learn(final AuthenticationToken authenticationToken, final Task task) throws SemanticMarkupException {
		String numberOfSentences = getNumberOfSentences();
		String numberOfWords = getNumberOfWords();
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);

		//browser back button may invoke another "learn"
		if(activeLearnFutures.containsKey(config.getConfiguration().getId())) {
			return new LearnInvocation(numberOfSentences, numberOfWords);
		} else {
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			daoManager.getTaskDAO().updateTask(task);
			
			String glossary = config.getGlossary().getName();
			final String input = config.getInput();
			String tablePrefix = String.valueOf(task.getId());
			String source = input; //maybe something else later
			String operator;
			try {
				operator = userService.getUser(authenticationToken).getFullNameEmailAffiliation();
			} catch (UserNotFoundException e1) {
				throw new SemanticMarkupException(task);
			}
			String bioportalUserId = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalUserId();
			String bioportalAPIKey = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalAPIKey();
			final Learn learn = new ExtraJvmLearn(authenticationToken, glossary, input, tablePrefix, source, operator, bioportalUserId, bioportalAPIKey);
			//final Learn learn = new InJvmLearn(authenticationToken, glossary, input, tablePrefix, source, operator, bioportalUserId, bioportalAPIKey);
			activeLearns.put(config.getConfiguration().getId(), learn);
			final ListenableFuture<LearnResult> futureResult = executorService.submit(learn);
			activeLearnFutures.put(config.getConfiguration().getId(), futureResult);
			futureResult.addListener(new Runnable() {
			     	public void run() {
			     		try {
				     		Learn learn = activeLearns.remove(config.getConfiguration().getId());
				 			ListenableFuture<LearnResult> futureResult = activeLearnFutures.remove(config.getConfiguration().getId());
				 			
				     		if(learn.isExecutedSuccessfully()) {
				     			if(!futureResult.isCancelled()) {
				     				LearnResult result = null;
									try {
										result = futureResult.get();
									} catch (InterruptedException | ExecutionException e) {
										log(LogLevel.ERROR, "Couldn't get the produced learn result", e);
									}
									if(result != null) {
						     			config.setOtoUploadId(result.getOtoUploadId());
						     			config.setOtoSecret(result.getOtoSecret());
						     			try {
											createOTOContexts(authenticationToken, task, result, input);
										} catch (SemanticMarkupException e1) { }
						     			try {
											otoCollectionService.initializeFromHistory(new Collection(result.getOtoUploadId(), result.getOtoSecret()));
										} catch (Exception e) {
											log(LogLevel.ERROR, "Couldn't initialize the uploaded oto dataset from history", e);
										}
						     			daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(config);
										TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
										task.setTaskStage(newTaskStage);
										task.setResumable(true);
										daoManager.getTaskDAO().updateTask(task);
										
										// send an email to the user who owns the task.
										sendFinishedLearningTermsEmail(task);
									}
				     			}
				     		} else {
				     			task.setFailed(true);
								task.setFailedTime(new Date());
								daoManager.getTaskDAO().updateTask(task);
				     		}
			     		} catch(Throwable t) {
				     		task.setFailed(true);
							task.setFailedTime(new Date());
							daoManager.getTaskDAO().updateTask(task);
			     		}
			     	}
			     }, executorService);
			
			return new LearnInvocation(numberOfSentences, numberOfWords);
		}
	}
	
	@Override
	public Task review(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		task = daoManager.getTaskDAO().getTask(task.getId());
		return task;
	}
	
	@Override
	public void parse(final AuthenticationToken authenticationToken, final Task task) throws SemanticMarkupException {		
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		
		if(activeParseFutures.containsKey(config.getConfiguration().getId())) {
			return;
		} else {
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PARSE_TEXT.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			daoManager.getTaskDAO().updateTask(task);
			
			String glossary = config.getGlossary().getName();
			String input = config.getInput();
			String tablePrefix = String.valueOf(task.getId());
			String source = input; //maybe something else later
			String operator;
			try {
				operator = userService.getUser(authenticationToken).getFullNameEmailAffiliation();
			} catch (UserNotFoundException e) {
				throw new SemanticMarkupException(task);
			}
			String bioportalUserId = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalUserId();
			String bioportalAPIKey = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalAPIKey();
			final Parse parse = new ExtraJvmParse(authenticationToken, glossary, input, tablePrefix, source, operator, bioportalUserId, bioportalAPIKey);
			//final Parse parse = new InJvmParse(authenticationToken, glossary, input, tablePrefix, source, operator, bioportalUserId, bioportalAPIKey);
			activeParses.put(config.getConfiguration().getId(), parse);
			final ListenableFuture<ParseResult> futureResult = executorService.submit(parse);
			activeParseFutures.put(config.getConfiguration().getId(), futureResult);
			futureResult.addListener(new Runnable() {
				@Override
				public void run() {
					try {
						Parse parse = activeParses.remove(config.getConfiguration().getId());
						ListenableFuture<ParseResult> futureResult = activeParseFutures.remove(config.getConfiguration().getId());
						if(parse.isExecutedSuccessfully()) {
							if(!futureResult.isCancelled()) {
								task.setResumable(true);
								//TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.TO_ONTOLOGIES.toString());
								TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.OUTPUT.toString());
								task.setTaskStage(newTaskStage);
								daoManager.getTaskDAO().updateTask(task);
								sendFinishedParsingEmail(task);
							}
						} else {
							task.setFailed(true);
							task.setFailedTime(new Date());
							daoManager.getTaskDAO().updateTask(task);
						}
					} catch(Throwable t) {
			     		task.setFailed(true);
						task.setFailedTime(new Date());
						daoManager.getTaskDAO().updateTask(task);
		     		}
				}
			}, executorService);
		}
	}
	
	@Override
	public Task output(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {	
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		config.setOutput(config.getInput() + "_output_by_TC_task_" + task.getName());
		
		String outputDirectory = config.getOutput();			
		String outputDirectoryParent;
		try {
			outputDirectoryParent = fileService.getParent(authenticationToken, outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		String outputDirectoryName;
		try {
			outputDirectoryName = fileService.getFileName(authenticationToken, outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		
		//find a suitable destination filePath
		String createDirectoy;
		try {
			createDirectoy = fileService.createDirectory(authenticationToken, outputDirectoryParent, 
					outputDirectoryName, true);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new SemanticMarkupException(task);
		}
		
		//copy the output files to the directory
		String charaParserOutputDirectory = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "out";		
		try {
			fileService.deleteFile(new AdminAuthenticationToken(), charaParserOutputDirectory + File.separator + "config.txt");
		} catch (PermissionDeniedException | FileDeleteFailedException e) {
			throw new SemanticMarkupException(task);
		}
		try {
			fileService.copyFiles(new AdminAuthenticationToken(), charaParserOutputDirectory, createDirectoy);
		} catch (CopyFilesFailedException | PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		
		//update task
		config.setOutput(createDirectoy);
		daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(config);
		task.setResumable(false);
		task.setComplete(true);
		task.setCompleted(new Date());
		daoManager.getTaskDAO().updateTask(task);
		
		daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectoy);
		
		return task;
	}
	
	@Override
	public boolean isValidInput(AuthenticationToken authenticationToken, String filePath) throws SemanticMarkupException {
		boolean readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission) 
			return false;
		boolean isDirectory;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		if(!isDirectory)
			return false;
		List<String> files;
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		for(String file : files) {
			boolean validResult;
			try {
				validResult = fileFormatService.isValidTaxonDescription(authenticationToken, filePath + File.separator + file);
			} catch (PermissionDeniedException | GetFileContentFailedException e) {
				throw new SemanticMarkupException();
			}
			if(!validResult)
				return false;
		}
		return true;
	}

	@Override
	public String saveOto(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		int uploadId = config.getOtoUploadId();
		String secret = config.getOtoSecret();
		
		Collection collection;
		try {
			collection = otoCollectionService.get(uploadId, secret);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get oto collection", e);
			throw new SemanticMarkupException(task);
		}
		
		String zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + "oto2" + 
				File.separator + task.getId() + File.separator + task.getName() + "_term_review";
		File file = new File(zipSource);
		file.mkdirs();
		createCategorizationFile(task, collection, zipSource);
		createSynonymFile(task, collection, zipSource);
		createCategoriesFile(task, collection, zipSource);
		
		String zipFilePath = zipSource + ".zip";
		Zipper zipper = new Zipper();
		zipFilePath = zipper.zip(zipSource, zipFilePath);
		if(zipFilePath != null)
			return zipFilePath;
		throw new SemanticMarkupException("Saving failed");
	}
	
	@Override
	public void importOto(Task task, String termCategorization, String synonymy) throws SemanticMarkupException {
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		int uploadId = config.getOtoUploadId();
		String secret = config.getOtoSecret();
		
		Collection collection = otoCollectionService.get(uploadId, secret);
		Map<String, Term> termsInCollection = new HashMap<String, Term>();
		for(Term term : collection.getTerms()) {
			termsInCollection.put(term.getTerm(), term);
		}
		Map<String, Label> labelsInCollection = new HashMap<String, Label>();
		for(Label label : collection.getLabels()) {
			labelsInCollection.put(label.getName(), label);
		}
		
		createTermCategorization(task, termCategorization, collection, termsInCollection, labelsInCollection);
		createSynonymy(task, synonymy, collection, termsInCollection, labelsInCollection);
		
		otoCollectionService.update(collection);
	}
	
	private void createSynonymy(Task task, String synonymy, Collection collection, Map<String, Term> termsInCollection, Map<String, Label> labelsInCollection) throws SemanticMarkupException {
		List<String[]> lines = new LinkedList<String[]>();
		try(CSVReader reader = new CSVReader(new StringReader(synonymy))) {
			lines = reader.readAll();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes reader", e);
			throw new SemanticMarkupException(task);
		}
		for(String[] line : lines) {
			String label = line[0].trim();
			String mainTerm = line[1].trim();
			String synonymTerm = line[2].trim();
			
			if(termsInCollection.containsKey(mainTerm) && termsInCollection.containsKey(synonymTerm) && 
					labelsInCollection.containsKey(label)) {
				Label collectionLabel = labelsInCollection.get(label);
				Term collectionMainTerm = termsInCollection.get(mainTerm);
				Term collectionSynonymTerm = termsInCollection.get(synonymTerm);
				collectionLabel.addSynonym(collectionMainTerm, collectionSynonymTerm, false);
			}
		}
	}

	private void createTermCategorization(Task task, String termCategorization, Collection collection, 
			Map<String, Term> termsInCollection, Map<String, Label> labelsInCollection) throws SemanticMarkupException {
		List<String[]> lines = new LinkedList<String[]>();
		try(CSVReader reader = new CSVReader(new StringReader(termCategorization))) {
			lines = reader.readAll();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes reader", e);
			throw new SemanticMarkupException(task);
		}
		
		for(String[] line : lines) {
			String label = line[0].trim();
			String term = line[1].trim();
			
			if(termsInCollection.containsKey(term) && labelsInCollection.containsKey(label)) {
				Term collectionTerm = termsInCollection.get(term);
				Label collectionLabel = labelsInCollection.get(label);
				
				List<Label> termsLabels = collection.getLabels(collectionTerm);
				for(Label termLabel : termsLabels) 
					termLabel.uncategorizeTerm(collectionTerm);
			}
		}
		
		for(String[] line : lines) {
			String label = line[0].trim();
			String term = line[1].trim();
			
			if(termsInCollection.containsKey(term) && labelsInCollection.containsKey(label)) {
				Term collectionTerm = termsInCollection.get(term);
				Label collectionLabel = labelsInCollection.get(label);
				
				collectionLabel.addMainTerm(collectionTerm);
			}
		}
	}

	private void createCategoriesFile(Task task, Collection collection, String destination) throws SemanticMarkupException {
		String path = destination + File.separator + "category_definition-task-" + task.getName() + ".csv";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not create file", e);
			throw new SemanticMarkupException(task);
		}
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(new String[] {"category", "definition"});
			for(Label label : collection.getLabels()) {
				csvWriter.writeNext(new String[] {label.getName(), label.getDescription()});
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes writer", e);
			throw new SemanticMarkupException(task);
		}
	}

	private void createSynonymFile(Task task, Collection collection, String destination) throws SemanticMarkupException {
		String path = destination + File.separator + "category_mainterm_synonymterm-task-" + task.getName() + ".csv";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not create file", e);
			throw new SemanticMarkupException(task);
		}
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(new String[] {"category", "term", "synonym"});
			for(Label label : collection.getLabels()) {
				for(Term mainTerm : label.getMainTerms()) {
					List<Term> synonyms = label.getSynonyms(mainTerm);
					for(Term synonym : synonyms) {
						csvWriter.writeNext(new String[] { label.getName(), mainTerm.getTerm(), synonym.getTerm() });
					}
				}
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes writer", e);
			throw new SemanticMarkupException(task);
		}
	}

	private void createCategorizationFile(Task task, Collection collection, String destination) throws SemanticMarkupException {
		String path = destination + File.separator + "category_term-task-" + task.getName() + ".csv";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not create file", e);
			throw new SemanticMarkupException(task);
		}
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(new String[] {"category", "term"});
			for(Label label : collection.getLabels()) {
				for(Term term : label.getTerms())
					csvWriter.writeNext(new String[] {label.getName(), term.getTerm()});
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes writer", e);
			throw new SemanticMarkupException(task);
		}
		
		/*ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String jsonCollection = writer.writeValueAsString(collection);
		try (FileWriter fileWriter = new FileWriter(new File(path))) {
			fileWriter.write(jsonCollection);
		}*/
	}

	/*@Override
	public RPCResult<Void> prepareOptionalOtoLiteSteps(AuthenticationToken authenticationToken, Task task) {
		try { 
			task = daoManager.getTaskDAO().getTask(task.getId());
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof SemanticMarkupConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;
			int uploadId = semanticMarkupConfiguration.getOtoUploadId();
			
			MarkupResultReader reader = new MarkupResultReader();
			String charaParserOutputDirectory = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "out";
			File input = new File(charaParserOutputDirectory);
			List<String> structures = reader.getStructures(input);
			List<Character> characters = reader.getCharacters(input);
			List<Character> rangeValueCharacters = reader.getRangeValueCharacters(input);
			
			
			//for to ontologies view
			TermCategoryPairDAO termCategoryPairDAO = TermCategoryPairDAO.getInstance();
			SynonymsDAO synonymsDAO = SynonymsDAO.getInstance();
			for(String structure : structures) {
				try {
					List<String> synonyms = synonymsDAO.getSynonyms(uploadId, structure, "structure");
					termCategoryPairDAO.addTermCategoryPair(uploadId, structure, "structure", synonyms);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			for(Character character : characters) {
				try {
					//character types such as range_value for e.g. size do not have a value attribute
					List<String> synonyms = synonymsDAO.getSynonyms(uploadId, character.getValue(), character.getCategory());
					termCategoryPairDAO.addTermCategoryPair(uploadId, character.getValue(), character.getCategory(), synonyms);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			//for hierarchies view
			StructuresDAO structuresDAO = StructuresDAO.getInstance();
			for(String structure : structures) {
				try {
					structuresDAO.addStructure(uploadId, structure);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			//for orders view
			OrderCategoriesDAO orderCategoriesDAO = OrderCategoriesDAO.getInstance();
			TermsInOrderCategoryDAO termsInOrderCategoryDAO = TermsInOrderCategoryDAO.getInstance();
			for(Character character : rangeValueCharacters) {
				int categoryId = orderCategoriesDAO.addOrderCategory(uploadId, character.getCategory());
				termsInOrderCategoryDAO.addTermsInOrderCategory(categoryId, character.getValue());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
		return new RPCResult<Void>(true);
	}*/
	

	@Override
	public List<Description> getDescriptions(AuthenticationToken authenticationToken, String filePath) {	
		List<Description> descriptions = new LinkedList<Description>();
		String fileContent;
		try {
			fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		} catch (PermissionDeniedException | GetFileContentFailedException e) {
			return null;
		}
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(fileContent)) {
			Document doc;
			try {
				doc = sax.build(reader);
			} catch (JDOMException | IOException e) {
				return null;
			}
			
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("//description", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> descriptionElements = xp.evaluate(doc);
			if(descriptionElements != null) 
				for(Element descriptionElement : descriptionElements) {
					String type = descriptionElement.getAttributeValue("type");
					String text = descriptionElement.getValue();
					descriptions.add(new Description(text, type));
				}
		}
		return descriptions;
	}
	
	@Override
	public Description getDescription(AuthenticationToken token, String filePath, int descriptionNumber) {
		List<Description> descriptions = getDescriptions(token, filePath);
		if(descriptions.size() > descriptionNumber)
			return descriptions.get(descriptionNumber);
		return null;
	}
	
	@Override
	public void setDescription(AuthenticationToken authenticationToken, String filePath, int descriptionNumber, String description) throws SemanticMarkupException {	
		String content;
		try {
			content = fileAccessService.getFileContent(authenticationToken, filePath);
		} catch (PermissionDeniedException | GetFileContentFailedException e) {
			throw new SemanticMarkupException();
		}
		String newContent;
		try {
			newContent = replaceDescription(content, description, descriptionNumber);
		} catch (JDOMException | IOException e1) {
			throw new SemanticMarkupException();
		}
		try {
			fileAccessService.setFileContent(authenticationToken, filePath, newContent);
		} catch (SetFileContentFailedException | PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
	}
	
	@Override
	public Task goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {		
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}

	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {		
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task !=  null && task.isResumable() && 
				task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP)) {
					//SemanticMarkupConfiguration configuration = daoManager.getSemanticMarkupConfigurationDAO().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
					return task;
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {		
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
			
		//remove matrix generation configuration
		if(config != null) {
			if(config.getInput() != null)
				try {
					fileService.setInUse(authenticationToken, false, config.getInput(), task);
				} catch (PermissionDeniedException e) {
					throw new SemanticMarkupException(task);
				}
			daoManager.getSemanticMarkupConfigurationDAO().remove(config);
		}
		
		//remove task
		if(task != null) {
			daoManager.getTaskDAO().removeTask(task);
			if(task.getConfiguration() != null)
				
				//remove configuration
				daoManager.getConfigurationDAO().remove(task.getConfiguration().getConfiguration());
		
			//cancel possible futures
			if(task.getTaskStage() != null) {
				switch(TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
				case INPUT:
					break;
				case LEARN_TERMS:
					if(activeLearnFutures.containsKey(config.getConfiguration().getId())) {
						ListenableFuture<LearnResult> learnFuture = activeLearnFutures.get(config.getConfiguration().getId());
						learnFuture.cancel(true);
					}
					if(activeLearns.containsKey(config.getConfiguration().getId())) {
						activeLearns.get(config.getConfiguration().getId()).destroy();
					}
					break;
				case OUTPUT:
					break;
				case PARSE_TEXT:
					if(activeParseFutures.containsKey(config.getConfiguration().getId())) {
						ListenableFuture<ParseResult> parseFuture = activeParseFutures.get(config.getConfiguration().getId());
						parseFuture.cancel(true);
					}
					if(activeParses.containsKey(config.getConfiguration().getId())) {
						activeParses.get(config.getConfiguration().getId()).destroy();
					}
					break;
				case PREPROCESS_TEXT:
					break;
				case REVIEW_TERMS:
					break;
				/*case TO_ONTOLOGIES:
					break;
				case HIERARCHY:
					break;
				case ORDERS: 
					break;*/
				default:
					break;
				}
			}
		}
	}

	@Override
	public void destroy() {
		this.executorService.shutdownNow();
		for(Learn learn : activeLearns.values()) {
			learn.destroy();
		}
		for(Parse parse : activeParses.values()) {
			parse.destroy();
		}
		super.destroy();
	}

	//TODO: Could also access the servlet instead of using oto's client
	private void createOTOContexts(AuthenticationToken authenticationToken, Task task, LearnResult learnResult, String input) throws SemanticMarkupException {
		edu.arizona.biosemantics.oto.client.oto2.Client client = new edu.arizona.biosemantics.oto.client.oto2.Client(Configuration.oto2Url);
		client.open();
		List<Context> contexts = new LinkedList<Context>();
		
		List<String> files = new LinkedList<String>();
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, input);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		for(String file : files) {
			List<Description> descriptions = getDescriptions(authenticationToken, input + File.separator + file);
			for(Description description : descriptions) {
				try {
					contexts.add(new Context(learnResult.getOtoUploadId(), getTaxonIdentification(authenticationToken, input + File.separator + file), 
							description.getContent()));
				} catch (PermissionDeniedException | GetFileContentFailedException e) {
					throw new SemanticMarkupException(task);
				}
			}
		}
		try {
			List<Context> result = client.putContexts(learnResult.getOtoUploadId(), learnResult.getOtoSecret(), contexts).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new SemanticMarkupException(task);
		}
		client.close();
	}
	
	private void sendFinishedLearningTermsEmail(Task task){
		String email = daoManager.getUserDAO().getUser(task.getUser().getId()).getEmail();
		String subject = Configuration.finishedSemanticMarkupLearnSubject.replace("<taskname>", task.getName());
		String body = Configuration.finishedSemanticMarkupLearnBody.replace("<taskname>", task.getName());
		emailer.sendEmail(email, subject, body);
	}
	
	private void sendFinishedParsingEmail(Task task){
		String email = daoManager.getUserDAO().getUser(task.getUser().getId()).getEmail();
		String subject = Configuration.finishedSemanticMarkupParseSubject.replace("<taskname>", task.getName());
		String body = Configuration.finishedSemanticMarkupParseBody.replace("<taskname>", task.getName());
		emailer.sendEmail(email, subject, body);
	}

	private String getNumberOfSentences() {
		return "XXXXX";
		//return 5423;
	}

	private String getNumberOfWords() {
		return "XXXXX";
		//return 21259;
	}
	
	
	private String getTaxonIdentification(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(fileContent)) {
			Document doc = null;
			try {
				doc = sax.build(reader);
			} catch (JDOMException | IOException e) {
				log(LogLevel.ERROR, "Could not build xml document", e);
			}
		
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("/bio:treatment/taxon_identification[@status='ACCEPTED']/taxon_name", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));

			if(doc != null) {
				List<Element> taxonIdentification = xp.evaluate(doc);
				return createTaxonIdentification(taxonIdentification);
			}
		}
		return null;
	}
	
	private String createTaxonIdentification(List<Element> taxonIdentifications) {
		//StringBuilder taxonNameBuilder = new StringBuilder();
		
		//the whole set of classes dealing with taxonomy building from plain text
		//should be moved in an own small project: functionality is currently shared
		//between matrix-generation, matrix-review, etc-site (for oto2)
		LinkedList<RankData> rankDatas = new LinkedList<RankData>();
		Map<Rank, RankData> rankDataMap = new HashMap<Rank, RankData>();
		for(Element taxonIdentification : taxonIdentifications) {
			String author = ""; //not used for oto2
			String data = ""; //not used for oto2
			Rank rank = Rank.valueOf(taxonIdentification.getAttributeValue("rank").toUpperCase());
			String name = taxonIdentification.getValue();
			RankData rankData = new RankData(rank, name, "", "");
			rankDatas.add(rankData);
			rankDataMap.put(rank, rankData);
			//taxonNameBuilder.append(taxonName.getAttributeValue("rank") + "=" + taxonName.getValue() + ",");
		}
		Collections.sort(rankDatas);
		if(rankDatas.contains(Rank.GENUS)) {
			String fullName = "";
			boolean found = false;
			for(int i=0; i<rankDatas.size(); i++) {
				RankData rankData = rankDatas.get(i);
				if(rankData.getRank().equals(Rank.GENUS)) {
					found = true;
				}
				if(found)
					fullName += rankData.getName() + " ";
			}
			return fullName.substring(0, fullName.length() - 1);
		} else {
			return rankDatas.getLast().getName();
		}
	}

	private String replaceDescription(String content, String description, int descriptionNumber) throws JDOMException, IOException {
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(content)) {
			Document doc = sax.build(reader);
			
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("//description", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> descriptions = xp.evaluate(doc);
			descriptions.get(descriptionNumber).setText(description);
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			return outputter.outputString(doc);
		}
	}

	private List<PreprocessedDescription> getPreprocessedDescriptions(AuthenticationToken authenticationToken, Task task) {
		List<PreprocessedDescription> result = new LinkedList<PreprocessedDescription>();
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		String inputDirectory = config.getInput();
		
		boolean isDirectory;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, inputDirectory);
		} catch (PermissionDeniedException e) {
			return result;
		}
		if(isDirectory) {
			List<String> directoryFiles;
			try {
				directoryFiles = fileService.getDirectoriesFiles(authenticationToken, inputDirectory);
			} catch (PermissionDeniedException e) {
				return result;
			}
			for(String file : directoryFiles) {
				List<Description> descriptions = getDescriptions(authenticationToken, inputDirectory + File.separator + file);
				for(int descriptionNumber = 0; descriptionNumber<descriptions.size(); descriptionNumber++) {
					String text = descriptions.get(descriptionNumber).getContent();
					if(!bracketValidator.validate(text)) {
						PreprocessedDescription preprocessedDescription = new PreprocessedDescription(
								inputDirectory + File.separator + file, file, descriptionNumber,
										bracketValidator.getBracketCountDifferences(text));
						result.add(preprocessedDescription);
					}
				}
			}
		}
		return result;
	}
	
	private SemanticMarkupConfiguration getSemanticMarkupConfiguration(Task task) {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof SemanticMarkupConfiguration))
			return null;
		return (SemanticMarkupConfiguration)configuration;
	}


	@Override
	public void renameTerm(AuthenticationToken token, Task task, String term, String newName) {
		SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		final String input = config.getInput();
		List<String> files = new LinkedList<String>();
		try {
			files = fileService.getDirectoriesFiles(token, input);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Couldn't access files of the task to rename terms", e);
		}
		for(String file : files) {
			List<Description> descriptions = getDescriptions(token, input + File.separator + file);
			for(int descriptionNumber = 0; descriptionNumber<descriptions.size(); descriptionNumber++) {
				String text = descriptions.get(descriptionNumber).getContent();
				text = text.replaceAll("(?i)\\b" + term + "\\b", newName);
				try {
					setDescription(token, input + File.separator + file, descriptionNumber, text);
				} catch (SemanticMarkupException e) {
					log(LogLevel.ERROR, "Couldnt' set description upon rename term", e);
				}
			}
		}
	}



}
