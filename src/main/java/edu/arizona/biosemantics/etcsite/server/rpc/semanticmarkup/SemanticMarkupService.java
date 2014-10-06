package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.rpc.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.Glossary;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.process.semanticmarkup.BracketValidator;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.ParseInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
/*import edu.arizona.biosemantics.etcsite.shared.db.otolite.OrderCategoriesDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.StructuresDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.SynonymsDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.TermCategoryPairDAO;
import edu.arizona.biosemantics.etcsite.shared.db.otolite.TermsInOrderCategoryDAO;*/
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.matrixgeneration.model.RankData;
import edu.arizona.biosemantics.matrixgeneration.model.Taxon.Rank;
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
	private IAuthenticationService authenticationService = new AuthenticationService();
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
	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName) {
		try {
			RPCResult<Boolean> sharedResult = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), filePath);
			if(!sharedResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't verify permission on input directory");
			RPCResult<String> fileNameResult = fileService.getFileName(authenticationToken, filePath);
			if(!fileNameResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't find file name for import");
			if(sharedResult.getData()) {
				RPCResult<String> destinationResult = 
						fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
								fileNameResult.getData(), true);
				RPCResult<Void> destination = fileService.copyFiles(authenticationToken, filePath, destinationResult.getData());
			
				if(!destinationResult.isSucceeded() || !destination.isSucceeded())
					return new RPCResult<Task>(false, "Couldn't copy shared files to an owned destination for input to task");
				filePath = destinationResult.getData();
			}
			
			RPCResult<List<String>> directoriesFilesResult = fileService.getDirectoriesFiles(authenticationToken, filePath);
			if(!directoriesFilesResult.isSucceeded()) {
				return new RPCResult<Task>(false, directoriesFilesResult.getMessage());
			}
			int numberOfInputFiles = directoriesFilesResult.getData().size();
			Glossary glossary = daoManager.getGlossaryDAO().getGlossary(glossaryName);
			SemanticMarkupConfiguration semanticMarkupConfiguration = new SemanticMarkupConfiguration();
			semanticMarkupConfiguration.setInput(filePath);	
			semanticMarkupConfiguration.setGlossary(glossary);
			semanticMarkupConfiguration.setNumberOfInputFiles(numberOfInputFiles);
			semanticMarkupConfiguration.setOutput(semanticMarkupConfiguration.getInput() + "_" + taskName);
			semanticMarkupConfiguration = daoManager.getSemanticMarkupConfigurationDAO().addSemanticMarkupConfiguration(semanticMarkupConfiguration);
			
			edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP;
			TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.INPUT.toString());
			ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			task.setTaskConfiguration(semanticMarkupConfiguration);
			task.setTaskType(dbTaskType);

			task = daoManager.getTaskDAO().addTask(task);
			taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
			List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
			if(result == null)
				return new RPCResult<Task>(false, "Not a compatible task");
			if(result.isEmpty())
				taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
			task.setTaskStage(taskStage);
			daoManager.getTaskDAO().updateTask(task);

			fileService.setInUse(authenticationToken, true, filePath, task);
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	private List<PreprocessedDescription> getPreprocessedDescriptions(AuthenticationToken authenticationToken, Task task) {
		List<PreprocessedDescription> result = new LinkedList<PreprocessedDescription>();
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof SemanticMarkupConfiguration))
			return null;
		final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;
		String inputDirectory = semanticMarkupConfiguration.getInput();
		
		RPCResult<Boolean> isDirectoryResult = fileService.isDirectory(authenticationToken, inputDirectory);
		if(isDirectoryResult.isSucceeded() && isDirectoryResult.getData()) {
			RPCResult<List<String>> resultDirectoryFiles = fileService.getDirectoriesFiles(authenticationToken, inputDirectory);
			if(resultDirectoryFiles.isSucceeded()) {
				for(String file : resultDirectoryFiles.getData()) {
					RPCResult<String> descriptionResult = getDescription(authenticationToken, inputDirectory + File.separator + file);
					if(descriptionResult.isSucceeded()) {
						String description = descriptionResult.getData();
						if(!bracketValidator.validate(description)) {
							PreprocessedDescription preprocessedDescription = new PreprocessedDescription(
									inputDirectory + File.separator + file,
									file, 0,
									bracketValidator.getBracketCountDifferences(description));
							result.add(preprocessedDescription);
						}	
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public RPCResult<List<PreprocessedDescription>> preprocess(AuthenticationToken authenticationToken, Task task) {	
		try {
			TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
			task.setTaskStage(taskStage);
			daoManager.getTaskDAO().updateTask(task);
			//do preprocessing here, return result immediately or always only return an invocation
			//and make user come back when ready?
			
			List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
			if(result == null)
				return new RPCResult<List<PreprocessedDescription>>(false, "Not a compatible task");
			return new RPCResult<List<PreprocessedDescription>>(true, result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<List<PreprocessedDescription>>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<LearnInvocation> learn(final AuthenticationToken authenticationToken, final Task task) {		
		try {
			String numberOfSentences = getNumberOfSentences();
			String numberOfWords = getNumberOfWords();
			
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof SemanticMarkupConfiguration))
				return new RPCResult<LearnInvocation>(false, "Not a compatible task");
			final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;

			//browser back button may invoke another "learn"
			if(activeLearnFutures.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
				return new RPCResult<LearnInvocation>(true, new LearnInvocation(numberOfSentences, numberOfWords));
			} else {
				final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
				TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				daoManager.getTaskDAO().updateTask(task);
				
				String glossary = semanticMarkupConfiguration.getGlossary().getName();
				final String input = semanticMarkupConfiguration.getInput();
				String tablePrefix = String.valueOf(task.getId());
				String source = input; //maybe something else later
				RPCResult<String> operatorResult = authenticationService.getOperator(authenticationToken);
				if(!operatorResult.isSucceeded())
					return new RPCResult<LearnInvocation>(false, operatorResult.getMessage());
				String bioportalUserId = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalUserId();
				String bioportalAPIKey = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalAPIKey();
				//Learn learn = new ExtraJvmLearn(authenticationToken, glossary, input, tablePrefix, source, operatorResult.getData(), bioportalUserId, bioportalAPIKey);
				Learn learn = new InJvmLearn(authenticationToken, glossary, input, tablePrefix, source, operatorResult.getData(), bioportalUserId, bioportalAPIKey);
				activeLearns.put(semanticMarkupConfiguration.getConfiguration().getId(), learn);
				final ListenableFuture<LearnResult> futureResult = executorService.submit(learn);
				activeLearnFutures.put(semanticMarkupConfiguration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			activeLearns.remove(semanticMarkupConfiguration.getConfiguration().getId());
				     			activeLearnFutures.remove(semanticMarkupConfiguration.getConfiguration().getId());
				     			if(!futureResult.isCancelled()) {
				     				LearnResult result = futureResult.get();
				     				
					     			semanticMarkupConfiguration.setOtoUploadId(result.getOtoUploadId());
					     			semanticMarkupConfiguration.setOtoSecret(result.getOtoSecret());
					     			
					     			createOTOContexts(authenticationToken, result, input);
					     			otoCollectionService.initializeFromHistory(new Collection(result.getOtoUploadId(), result.getOtoSecret()));
					     			daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(semanticMarkupConfiguration);
									TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									daoManager.getTaskDAO().updateTask(task);
									
									// send an email to the user who owns the task.
									sendFinishedLearningTermsEmail(task);
				     			}
							} catch (Exception e) {
								e.printStackTrace();
							}
				     	}
				     }, executorService);
				
				return new RPCResult<LearnInvocation>(true, new LearnInvocation(numberOfSentences, numberOfWords));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<LearnInvocation>(false, "Internal Server Error");
		}
	}
	
	//TODO: Could also access the servlet instead of using oto's client
	private void createOTOContexts(AuthenticationToken authenticationToken, LearnResult learnResult, String input) {
		edu.arizona.biosemantics.oto.client.oto2.Client client = new edu.arizona.biosemantics.oto.client.oto2.Client(Configuration.deploymentUrl);
		client.open();
		List<Context> contexts = new LinkedList<Context>();
		
		RPCResult<List<String>> files = fileService.getDirectoriesFiles(authenticationToken, input);
		if(files.isSucceeded())
			for(String file : files.getData()) {
				RPCResult<String> description = getDescription(authenticationToken, input + File.separator + file);
				if(description.isSucceeded())
					contexts.add(new Context(learnResult.getOtoUploadId(), getTaxonName(authenticationToken, input + File.separator + file), description.getData()));
			}
		try {
			List<Context> result = client.putContexts(learnResult.getOtoUploadId(), learnResult.getOtoSecret(), contexts).get();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
	}
	
	private void sendFinishedLearningTermsEmail(Task task){
		try {
			String email = daoManager.getUserDAO().getUser(task.getUser().getId()).getEmail();
			String subject = Configuration.finishedSemanticMarkupLearnSubject.replace("<taskname>", task.getName());
			String body = Configuration.finishedSemanticMarkupLearnBody.replace("<taskname>", task.getName());
			
			emailer.sendEmail(email, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void sendFinishedParsingEmail(Task task){
		try {
			String email = daoManager.getUserDAO().getUser(task.getUser().getId()).getEmail();
			String subject = Configuration.finishedSemanticMarkupParseSubject.replace("<taskname>", task.getName());
			String body = Configuration.finishedSemanticMarkupParseBody.replace("<taskname>", task.getName());
			
			emailer.sendEmail(email, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private String getNumberOfSentences() {
		return "XXXXX";
		//return 5423;
	}

	private String getNumberOfWords() {
		return "XXXXX";
		//return 21259;
	}
	
	@Override
	public RPCResult<Task> review(AuthenticationToken authenticationToken, Task task) {
		try {
			TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
			task.setTaskStage(taskStage);
			daoManager.getTaskDAO().updateTask(task);
			task = daoManager.getTaskDAO().getTask(task.getId());
			return new RPCResult<Task>(true, task);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
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
	public RPCResult<ParseInvocation> parse(final AuthenticationToken authenticationToken, final Task task) {		
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof SemanticMarkupConfiguration))
				return new RPCResult<ParseInvocation>(false, "Not a compatible task");
			final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;
			
			if(activeParseFutures.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
				return new RPCResult<ParseInvocation>(true, new ParseInvocation());
			} else {
				final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
				TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PARSE_TEXT.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				daoManager.getTaskDAO().updateTask(task);
				
				String glossary = semanticMarkupConfiguration.getGlossary().getName();
				String input = semanticMarkupConfiguration.getInput();
				String tablePrefix = String.valueOf(task.getId());
				String source = input; //maybe something else later
				RPCResult<String> operator = authenticationService.getOperator(authenticationToken);
				if(!operator.isSucceeded())
					return new RPCResult<ParseInvocation>(false, operator.getMessage());
				String bioportalUserId = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalUserId();
				String bioportalAPIKey = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalAPIKey();
				Parse parse = new ExtraJvmParse(authenticationToken, glossary, input, tablePrefix, source, operator.getData(), bioportalUserId, bioportalAPIKey);
				activeParses.put(semanticMarkupConfiguration.getConfiguration().getId(), parse);
				final ListenableFuture<ParseResult> futureResult = executorService.submit(parse);
				activeParseFutures.put(semanticMarkupConfiguration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
					@Override
					public void run() {
						try {
							activeParses.remove(semanticMarkupConfiguration.getConfiguration().getId());
							activeParseFutures.remove(semanticMarkupConfiguration.getConfiguration().getId());
							if(!futureResult.isCancelled()) {
								task.setResumable(true);
								//TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.TO_ONTOLOGIES.toString());
								TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.OUTPUT.toString());
								task.setTaskStage(newTaskStage);
								daoManager.getTaskDAO().updateTask(task);
								sendFinishedParsingEmail(task);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, executorService);
				
				return new RPCResult<ParseInvocation>(true, new ParseInvocation());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<ParseInvocation>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Task> output(AuthenticationToken authenticationToken, Task task) {		
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof SemanticMarkupConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;
			semanticMarkupConfiguration.setOutput(semanticMarkupConfiguration.getInput() + "_" + task.getName());
			
			String outputDirectory = semanticMarkupConfiguration.getOutput();			
			RPCResult<String> outputDirectoryParentResult = fileService.getParent(authenticationToken, outputDirectory);
			RPCResult<String> outputDirectoryNameResult = fileService.getFileName(authenticationToken, outputDirectory);
			if(!outputDirectoryParentResult.isSucceeded() || !outputDirectoryNameResult.isSucceeded())
				return new RPCResult<Task>(false, outputDirectoryParentResult.getMessage());
			
			//find a suitable destination filePath
			RPCResult<String> createDirectoryResult = fileService.createDirectory(authenticationToken, outputDirectoryParentResult.getData(), 
					outputDirectoryNameResult.getData(), true);
			if(!createDirectoryResult.isSucceeded()) 
				return new RPCResult<Task>(false, createDirectoryResult.getMessage());
			
			//copy the output files to the directory
			String charaParserOutputDirectory = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "out";		
			RPCResult<Void> deleteResult = fileService.deleteFile(new AdminAuthenticationToken(), charaParserOutputDirectory + File.separator + "config.txt");
			if(!deleteResult.isSucceeded()) {
				return new RPCResult<Task>(false, deleteResult.getMessage());
			}
			RPCResult<Void> copyResult = fileService.copyFiles(new AdminAuthenticationToken(), charaParserOutputDirectory, createDirectoryResult.getData());
			if(!copyResult.isSucceeded()) {
				return new RPCResult<Task>(false, copyResult.getMessage());
			}
			
			//update task
			semanticMarkupConfiguration.setOutput(createDirectoryResult.getData());
			daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(semanticMarkupConfiguration);
			task.setResumable(false);
			task.setComplete(true);
			task.setCompleted(new Date());
			daoManager.getTaskDAO().updateTask(task);
			
			daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectoryResult.getData());
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {		
		try {
			TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(taskStageEnum.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			task.setComplete(false);
			task.setCompleted(null);
			daoManager.getTaskDAO().updateTask(task);
			return new RPCResult<Task>(true, task);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}	

	@Override
	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String filePath) {	
		try {
			RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
			if(fileContentResult.isSucceeded()) {
				SAXBuilder sax = new SAXBuilder();
				String fileContent = fileContentResult.getData();
				try(StringReader reader = new StringReader(fileContent)) {
					Document doc = sax.build(reader);
					
					XPathFactory xpfac = XPathFactory.instance();
					XPathExpression<Element> xp = xpfac.compile("/bio:treatment/description", Filters.element(), null,
							Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
					List<Element> descriptions = xp.evaluate(doc);
					if(descriptions != null && !descriptions.isEmpty()) {
						StringBuilder fullDescription = new StringBuilder();
						for(Element description : descriptions)
							fullDescription.append(description.getText() + "\n\n");
						String fullDescr = fullDescription.toString();
						return new RPCResult<String>(true, "", fullDescription.toString().substring(0, fullDescr.length() - 2));
					} else 
						return new RPCResult<String>(false, "No description found in this file", null);
				}
			}
			return new RPCResult<String>(false, fileContentResult.getMessage(), "");
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Internal Server Error", "");
		}
	}
	
	private String getTaxonName(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			SAXBuilder sax = new SAXBuilder();
			String fileContent = fileContentResult.getData();
			try(StringReader reader = new StringReader(fileContent)) {
				try {
					Document doc = sax.build(reader);
				
					XPathFactory xpfac = XPathFactory.instance();
					XPathExpression<Element> xp = xpfac.compile("/bio:treatment/taxon_identification[@status='ACCEPTED']/taxon_name", Filters.element(), null,
							Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
					List<Element> taxonNames = xp.evaluate(doc);
					return createTaxonName(taxonNames);
				} catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}
	
	private String createTaxonName(List<Element> taxonNames) {
		//StringBuilder taxonNameBuilder = new StringBuilder();
		
		//the whole set of classes dealing with taxonomy building from plain text
		//should be moved in an own small project: functionality is currently shared
		//between matrix-generation, matrix-review, etc-site (for oto2)
		LinkedList<RankData> rankDatas = new LinkedList<RankData>();
		Map<Rank, RankData> rankDataMap = new HashMap<Rank, RankData>();
		for(Element taxonName : taxonNames) {
			String author = ""; //not used for oto2
			String data = ""; //not used for oto2
			Rank rank = Rank.valueOf(taxonName.getAttributeValue("rank").toUpperCase());
			String name = taxonName.getValue();
			RankData rankData = new RankData("", "", rank, name);
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

	private String replaceDescription(String content, String description) throws Exception {
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(content)) {
			Document doc = sax.build(reader);
			
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("/bio:treatment/description", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> descriptions = xp.evaluate(doc);
			if(descriptions != null && !descriptions.isEmpty())
				descriptions.get(0).setText(description);
			
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			return outputter.outputString(doc);
		}
	}

	@Override
	public RPCResult<Void> setDescription(AuthenticationToken authenticationToken, String filePath, String description) {	
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String content = fileContentResult.getData();		
			String newContent;
			try {
				newContent = replaceDescription(content, description);
				RPCResult<Void> setContentResult = fileAccessService.setFileContent(authenticationToken, filePath, newContent);
				if(!setContentResult.isSucceeded()) 
					return new RPCResult<Void>(false, setContentResult.getMessage());
				return new RPCResult<Void>(true);
			} catch (Exception e) {
				e.printStackTrace();
				return new RPCResult<Void>(false, "Internal Server Error");
			}
		}
		return new RPCResult<Void>(false, fileContentResult.getMessage());
	}

	@Override
	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken) {		
		try {
			ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
			List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
			for(Task task : tasks) {
				if(task.isResumable() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP)) {
						//SemanticMarkupConfiguration configuration = daoManager.getSemanticMarkupConfigurationDAO().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
						return new RPCResult<Task>(true, task);
				}
			}
			return new RPCResult<Task>(true, "", null);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task) {		
		try {		
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof SemanticMarkupConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;
			
			//remove matrix generation configuration
			if(semanticMarkupConfiguration != null) {
				if(semanticMarkupConfiguration.getInput() != null)
					fileService.setInUse(authenticationToken, false, semanticMarkupConfiguration.getInput(), task);
				daoManager.getSemanticMarkupConfigurationDAO().remove(semanticMarkupConfiguration);
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
						if(activeLearnFutures.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
							ListenableFuture<LearnResult> learnFuture = activeLearnFutures.get(semanticMarkupConfiguration.getConfiguration().getId());
							learnFuture.cancel(true);
						}
						if(activeLearns.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
							activeLearns.get(semanticMarkupConfiguration.getConfiguration().getId()).destroy();
						}
						break;
					case OUTPUT:
						break;
					case PARSE_TEXT:
						if(activeParseFutures.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
							ListenableFuture<ParseResult> parseFuture = activeParseFutures.get(semanticMarkupConfiguration.getConfiguration().getId());
							parseFuture.cancel(true);
						}
						if(activeParses.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
							activeParses.get(semanticMarkupConfiguration.getConfiguration().getId()).destroy();
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
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
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

	@Override
	public RPCResult<Boolean> isValidInput(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<Boolean> readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission.isSucceeded())
			return new RPCResult<Boolean>(false, readPermission.getMessage());
		if(!readPermission.getData()) 
			return new RPCResult<Boolean>(true, false);
		RPCResult<Boolean> directoryResult = fileService.isDirectory(authenticationToken, filePath);
		if(!directoryResult.isSucceeded())
			return new RPCResult<Boolean>(false, directoryResult.getMessage());
		if(!directoryResult.getData())
			return new RPCResult<Boolean>(true, false);
		RPCResult<List<String>> filesResult = fileService.getDirectoriesFiles(authenticationToken, filePath);
		if(!filesResult.isSucceeded())
			return new RPCResult<Boolean>(false, filesResult.getMessage());
		List<String> files = filesResult.getData();
		for(String file : files) {
			RPCResult<Boolean> validResult = fileFormatService.isValidTaxonDescription(authenticationToken, filePath + File.separator + file);
			if(!validResult.isSucceeded())
				return new RPCResult<Boolean>(false, validResult.getMessage());
			if(!validResult.getData())
				return new RPCResult<Boolean>(true, false);
		}
		return new RPCResult<Boolean>(true, true);
	}

	@Override
	public RPCResult<String> saveOto(AuthenticationToken authenticationToken, 
			Task task) {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof SemanticMarkupConfiguration))
			return null;
		final SemanticMarkupConfiguration semanticMarkupConfiguration = (SemanticMarkupConfiguration)configuration;
		int uploadId = semanticMarkupConfiguration.getOtoUploadId();
		String secret = semanticMarkupConfiguration.getOtoSecret();
		
		try {
			Collection collection = otoCollectionService.get(uploadId, secret);			
			String path = Configuration.tempFiles + 
					File.separator + "oto2" + File.separator + authenticationToken.getUserId() +
					File.separator + "term_categorizations_task-" + task.getName() + ".csv";
			File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
				csvWriter.writeNext(new String[] {"term", "category"});
				for(Label label : collection.getLabels()) {
					for(Term term : label.getTerms())
					csvWriter.writeNext(new String[] {term.getTerm(), label.getName() });
				}
				csvWriter.writeNext(new String[] { });
				csvWriter.writeNext(new String[] {"category", "definition"});
				for(Label label : collection.getLabels()) {
					csvWriter.writeNext(new String[] {label.getName(), label.getDescription()});
				}
			}
			
			/*ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			String jsonCollection = writer.writeValueAsString(collection);
			try (FileWriter fileWriter = new FileWriter(new File(path))) {
				fileWriter.write(jsonCollection);
			}*/
			return new RPCResult<String>(true, "", path);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Internal Server Error" ,"");
		}
	}

}
