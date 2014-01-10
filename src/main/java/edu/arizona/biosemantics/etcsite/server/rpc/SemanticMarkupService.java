package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.db.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.ConfigurationDAO;
import edu.arizona.biosemantics.etcsite.shared.db.Glossary;
import edu.arizona.biosemantics.etcsite.shared.db.GlossaryDAO;
import edu.arizona.biosemantics.etcsite.shared.db.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.SemanticMarkupConfigurationDAO;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.db.TaskDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.db.TaskStageDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TaskType;
import edu.arizona.biosemantics.etcsite.shared.db.TaskTypeDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TasksOutputFilesDAO;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.BracketValidator;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.ParseInvocation;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;

public class SemanticMarkupService extends RemoteServiceServlet implements ISemanticMarkupService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private BracketValidator bracketValidator = new BracketValidator();
	private int maximumThreads = 10;
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<LearnResult>> activeLearnFutures = new HashMap<Integer, ListenableFuture<LearnResult>>();
	private Map<Integer, ListenableFuture<ParseResult>> activeParseFutures = new HashMap<Integer, ListenableFuture<ParseResult>>();

	
	public SemanticMarkupService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
	}
	
	@Override
	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName) {
		try {
			RPCResult<Boolean> sharedResult = filePermissionService.isSharedFilePath(authenticationToken.getUsername(), filePath);
			if(!sharedResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't verify permission on input directory");
			RPCResult<String> fileNameResult = fileService.getFileName(authenticationToken, filePath);
			if(!fileNameResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't find file name for import");
			if(sharedResult.getData()) {
				RPCResult<String> destinationResult = 
						fileService.createDirectoryForcibly(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUsername(), fileNameResult.getData());
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
			Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryName);
			SemanticMarkupConfiguration semanticMarkupConfiguration = new SemanticMarkupConfiguration();
			semanticMarkupConfiguration.setInput(filePath);	
			semanticMarkupConfiguration.setGlossary(glossary);
			semanticMarkupConfiguration.setNumberOfInputFiles(numberOfInputFiles);
			semanticMarkupConfiguration.setOutput(semanticMarkupConfiguration.getInput() + "_" + taskName);
			semanticMarkupConfiguration = SemanticMarkupConfigurationDAO.getInstance().addSemanticMarkupConfiguration(semanticMarkupConfiguration);
			
			edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP;
			TaskType dbTaskType = TaskTypeDAO.getInstance().getTaskType(taskType);
			TaskStage taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.INPUT.toString());
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			task.setTaskConfiguration(semanticMarkupConfiguration);
			task.setTaskType(dbTaskType);

			task = TaskDAO.getInstance().addTask(task);
			taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
			List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
			if(result == null)
				return new RPCResult<Task>(false, "Not a compatible task");
			if(result.isEmpty())
				taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
			task.setTaskStage(taskStage);
			TaskDAO.getInstance().updateTask(task);

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
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
			task.setTaskStage(taskStage);
			TaskDAO.getInstance().updateTask(task);
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
	public RPCResult<LearnInvocation> learn(AuthenticationToken authenticationToken, final Task task) {		
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
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP);
				TaskStage taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				String glossary = semanticMarkupConfiguration.getGlossary().getName();
				String input = semanticMarkupConfiguration.getInput();
				String tablePrefix = String.valueOf(task.getId());
				String source = input; //maybe something else later
				String user = authenticationToken.getUsername();
				String bioportalUserId = UserDAO.getInstance().getUser(authenticationToken.getUsername()).getBioportalUserId();
				String bioportalAPIKey = UserDAO.getInstance().getUser(authenticationToken.getUsername()).getBioportalAPIKey();
				ILearn learn = new Learn(authenticationToken, glossary, input, tablePrefix, source, user, bioportalUserId, bioportalAPIKey);
				final ListenableFuture<LearnResult> futureResult = executorService.submit(learn);
				activeLearnFutures.put(semanticMarkupConfiguration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			activeLearnFutures.remove(semanticMarkupConfiguration.getConfiguration().getId());
				     			if(!futureResult.isCancelled()) {
				     				LearnResult result = futureResult.get();
					     			semanticMarkupConfiguration.setOtoUploadId(result.getOtoUploadId());
					     			semanticMarkupConfiguration.setOtoSecret(result.getOtoSecret());
									SemanticMarkupConfigurationDAO.getInstance().updateSemanticMarkupConfiguration(semanticMarkupConfiguration);
									TaskStage newTaskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									TaskDAO.getInstance().updateTask(task);
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
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
			task.setTaskStage(taskStage);
			TaskDAO.getInstance().updateTask(task);
			task = TaskDAO.getInstance().getTask(task.getId());
			return new RPCResult<Task>(true, task);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
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
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP);
				TaskStage taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.PARSE_TEXT.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				String glossary = semanticMarkupConfiguration.getGlossary().getName();
				String input = semanticMarkupConfiguration.getInput();
				String tablePrefix = String.valueOf(task.getId());
				String source = input; //maybe something else later
				String user = authenticationToken.getUsername();
				String bioportalUserId = UserDAO.getInstance().getUser(authenticationToken.getUsername()).getBioportalUserId();
				String bioportalAPIKey = UserDAO.getInstance().getUser(authenticationToken.getUsername()).getBioportalAPIKey();
				IParse parse = new Parse(authenticationToken, glossary, input, tablePrefix, source, user, bioportalUserId, bioportalAPIKey);
				final ListenableFuture<ParseResult> futureResult = executorService.submit(parse);
				activeParseFutures.put(semanticMarkupConfiguration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
					@Override
					public void run() {
						try {
							activeParseFutures.remove(semanticMarkupConfiguration.getConfiguration().getId());
							if(!futureResult.isCancelled()) {
								task.setResumable(true);
								TaskStage newTaskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(TaskStageEnum.OUTPUT.toString());
								task.setTaskStage(newTaskStage);
								TaskDAO.getInstance().updateTask(task);
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
			RPCResult<String> createDirectoryResult = fileService.createDirectoryForcibly(authenticationToken, outputDirectoryParentResult.getData(), outputDirectoryNameResult.getData());
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
			SemanticMarkupConfigurationDAO.getInstance().updateSemanticMarkupConfiguration(semanticMarkupConfiguration);
			task.setResumable(false);
			task.setComplete(true);
			task.setCompleted(new Date());
			TaskDAO.getInstance().updateTask(task);
			
			TasksOutputFilesDAO.getInstance().addOutput(task, createDirectoryResult.getData());
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {		
		try {
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = TaskStageDAO.getInstance().getSemanticMarkupTaskStage(taskStageEnum.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			task.setComplete(false);
			task.setCompleted(null);
			TaskDAO.getInstance().updateTask(task);
			return new RPCResult<Task>(true, task);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}	

	@Override
	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String filePath) {	
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
			if(fileContentResult.isSucceeded()) {
				String fileContent = fileContentResult.getData();
				Document document = db.parse(new InputSource(new ByteArrayInputStream(fileContent.getBytes("UTF-8"))));
				XPath xPath = XPathFactory.newInstance().newXPath();
				Node node = (Node)xPath.evaluate("/treatment/description",
				        document.getDocumentElement(), XPathConstants.NODE);
				return new RPCResult<String>(true, "", node.getTextContent());
			}
			return new RPCResult<String>(false, fileContentResult.getMessage(), "");
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Internal Server Error", "");
		}
	}
	
	private String replaceDescription(String content, String description) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(new InputSource(new ByteArrayInputStream(content.getBytes("UTF-8"))));
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node node = (Node)xPath.evaluate("/treatment/description",
		        document.getDocumentElement(), XPathConstants.NODE);
		node.setTextContent(description);

		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		//return xmlFileFormatter.format(writer.toString());
		return writer.toString();
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
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Task> tasks = TaskDAO.getInstance().getOwnedTasks(user.getId());
			for(Task task : tasks) {
				if(task.isResumable() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.SEMANTIC_MARKUP)) {
						//SemanticMarkupConfiguration configuration = SemanticMarkupConfigurationDAO.getInstance().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
						return new RPCResult<Task>(true, task);
				}
			}
			return new RPCResult<Task>(false, "No resumable task found");
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
				SemanticMarkupConfigurationDAO.getInstance().remove(semanticMarkupConfiguration);
			}
			
			//remove task
			if(task != null) {
				TaskDAO.getInstance().removeTask(task);
				if(task.getConfiguration() != null)
					
					//remove configuration
					ConfigurationDAO.getInstance().remove(task.getConfiguration().getConfiguration());
			
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
						break;
					case OUTPUT:
						break;
					case PARSE_TEXT:
						if(activeParseFutures.containsKey(semanticMarkupConfiguration.getConfiguration().getId())) {
							ListenableFuture<ParseResult> parseFuture = activeParseFutures.get(semanticMarkupConfiguration.getConfiguration().getId());
							parseFuture.cancel(true);
						}
						break;
					case PREPROCESS_TEXT:
						break;
					case REVIEW_TERMS:
						break;
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
		super.destroy();
	}
}
