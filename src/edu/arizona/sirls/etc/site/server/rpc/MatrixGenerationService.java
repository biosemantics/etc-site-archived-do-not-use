package edu.arizona.sirls.etc.site.server.rpc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
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

import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.db.Glossary;
import edu.arizona.sirls.etc.site.shared.rpc.db.GlossaryDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfigurationDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskStage;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskStageDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskType;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaskTypeDAO;
import edu.arizona.sirls.etc.site.shared.rpc.db.User;
import edu.arizona.sirls.etc.site.shared.rpc.db.UserDAO;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLFileFormatter;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.BracketValidator;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private ITaskService taskService = new TaskService();
	private IFileService fileService = new FileService();
	private XMLFileFormatter xmlFileFormatter = new XMLFileFormatter();
	private BracketValidator bracketValidator = new BracketValidator();
	private int maximumThreads = 10;
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<LearnResult>> activeLearnFutures = new HashMap<Integer, ListenableFuture<LearnResult>>();
	private Map<Integer, ListenableFuture<ParseResult>> activeParseFutures = new HashMap<Integer, ListenableFuture<ParseResult>>();
	
	public MatrixGenerationService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(maximumThreads));
	}
	
	@Override
	public MatrixGenerationConfiguration start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName) {
		try {
			edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum taskType = edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION;
			TaskType dbTaskType = TaskTypeDAO.getInstance().getTaskType(taskType);
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(dbTaskType, TaskStageEnum.INPUT);
			User user = UserDAO.getInstance().getUser(authenticationToken.getUsername());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			task = taskService.addTask(authenticationToken, task);
			
			Glossary glossary = GlossaryDAO.getInstance().getGlossary(glossaryName);
			MatrixGenerationConfiguration matrixGenerationConfiguration = new MatrixGenerationConfiguration();
			matrixGenerationConfiguration.setInput(input);	
			matrixGenerationConfiguration.setGlossary(glossary);
			matrixGenerationConfiguration.setTask(task);
			matrixGenerationConfiguration = 
					MatrixGenerationConfigurationDAO.getInstance().addMatrixGenerationConfiguration(matrixGenerationConfiguration);
			return matrixGenerationConfiguration;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken,
			MatrixGenerationConfiguration matrixGenerationConfiguration) {
		List<PreprocessedDescription> result = new LinkedList<PreprocessedDescription>();
		
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				Task task = matrixGenerationConfiguration.getTask();
				TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskType, TaskStageEnum.PREPROCESS_TEXT);
				task.setTaskStage(taskStage);
				TaskDAO.getInstance().updateTask(task);
				//do preprocessing here, return result immediately or always only return an invocation
				//and make user come back when ready?
				String inputDirectory = matrixGenerationConfiguration.getInput();
				
				if(fileService.isDirectory(authenticationToken, inputDirectory)) {
					List<String> files = fileService.getDirectoriesFiles(authenticationToken, inputDirectory);
					for(String file : files) {
						String description = getDescription(authenticationToken, inputDirectory + "//" + file);
						if(!bracketValidator.validate(description)) {
							PreprocessedDescription preprocessedDescription = new PreprocessedDescription(
									inputDirectory + "//" + file,
									file, 0,
									bracketValidator.getBracketCountDifferences(description));
							result.add(preprocessedDescription);
						}	
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	@Override
	public LearnInvocation learn(AuthenticationToken authenticationToken, final MatrixGenerationConfiguration matrixGenerationConfiguration) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				final Task task = matrixGenerationConfiguration.getTask();
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskType, TaskStageEnum.LEARN_TERMS);
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				int numberOfSentences = getNumberOfSentences();
				int numberOfWords = getNumberOfWords();
				
				String config = matrixGenerationConfiguration.getGlossary().getName();
				String input = matrixGenerationConfiguration.getInput();
				String tablePrefix = String.valueOf(task.getId());
				ILearn learn = new Learn(authenticationToken, config, input, tablePrefix);
				final ListenableFuture<LearnResult> futureResult = executorService.submit(learn);
				activeLearnFutures.put(matrixGenerationConfiguration.getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			matrixGenerationConfiguration.setOtoId(futureResult.get().getOtoId());
								MatrixGenerationConfigurationDAO.getInstance().updateMatrixGenerationConfiguration(matrixGenerationConfiguration);
								task.setResumable(true);
								TaskDAO.getInstance().updateTask(task);
							} catch (Exception e) {
								e.printStackTrace();
							}
				     	}
				     }, executorService);
				
				return new LearnInvocation(numberOfSentences, numberOfWords);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private int getNumberOfSentences() {
		return 5423;
	}

	private int getNumberOfWords() {
		return 21259;
	}

	@Override
	public String review(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				Task task = matrixGenerationConfiguration.getTask();
				TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskType, TaskStageEnum.REVIEW_TERMS);
				task.setTaskStage(taskStage);
				TaskDAO.getInstance().updateTask(task);
				MatrixGenerationConfiguration configuration = 
						MatrixGenerationConfigurationDAO.getInstance().getMatrixGenerationConfiguration(
								matrixGenerationConfiguration.getId());
				String result = "http://biosemantics.arizona.edu:8080/OTOLite/?uploadID=" + configuration.getOtoId();
				return result;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public ParseInvocation parse(final AuthenticationToken authenticationToken, final MatrixGenerationConfiguration matrixGenerationConfiguration) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				final Task task = matrixGenerationConfiguration.getTask();
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskType, TaskStageEnum.PARSE_TEXT);
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				String config = matrixGenerationConfiguration.getGlossary().getName();
				String input = matrixGenerationConfiguration.getInput();
				String tablePrefix = String.valueOf(task.getId());
				IParse parse = new Parse(authenticationToken, config, input, tablePrefix);
				final ListenableFuture<ParseResult> futureResult = executorService.submit(parse);
				activeParseFutures.put(matrixGenerationConfiguration.getId(), futureResult);
				futureResult.addListener(new Runnable() {
					@Override
					public void run() {
						try {
							task.setResumable(true);
							TaskDAO.getInstance().updateTask(task);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, executorService);
				
				return new ParseInvocation();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean output(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				// TODO: create a directory parallel to input directory with name "input dir name" + _MGResult.. with result files inside
				//result = fileService.createFile(authenticationToken, matrixGenerationJob..getOutputFile());
				
				Task task = matrixGenerationConfiguration.getTask();
				matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + task.getName());
				MatrixGenerationConfigurationDAO.getInstance().updateMatrixGenerationConfiguration(matrixGenerationConfiguration);

				String outputDirectory = matrixGenerationConfiguration.getOutput();
				String target = outputDirectory.substring(0, outputDirectory.lastIndexOf("//"));
				String newDirectory = outputDirectory.substring(outputDirectory.lastIndexOf("//"), outputDirectory.length());
				result = fileService.createDirectory(authenticationToken, target, newDirectory);
				
				File outDirectory = new File("workspace" + File.separator + task.getId() + File.separator + "out");
				for(File outFile : outDirectory.listFiles()) {
					File targetFile = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + 
							matrixGenerationConfiguration.getOutput() + "//" + outFile.getName());
					Files.copy(outFile, targetFile);
				}
				
				
				TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(taskType, TaskStageEnum.OUTPUT);
				task.setTaskStage(taskStage);
				task.setResumable(false);
				task.setCompleted(true);
				TaskDAO.getInstance().updateTask(task);
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String getDescription(AuthenticationToken authenticationToken, String target) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String fileContent = fileAccessService.getFileContent(authenticationToken, target);
			Document document = db.parse(new InputSource(new ByteArrayInputStream(fileContent.getBytes("utf-8"))));
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node node = (Node)xPath.evaluate("/treatment/description",
			        document.getDocumentElement(), XPathConstants.NODE);
			return node.getTextContent();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String replaceDescription(String content, String description) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new ByteArrayInputStream(content.getBytes("utf-8"))));
			
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public boolean setDescription(AuthenticationToken authenticationToken, String target, String description) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			String content = fileAccessService.getFileContent(authenticationToken, target);
			String newContent = replaceDescription(content, description);
			result = fileAccessService.setFileContent(authenticationToken, target, newContent);
		}
		return result;
	}

	@Override
	public MatrixGenerationConfiguration getLatestResumable(AuthenticationToken authenticationToken) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				User user = UserDAO.getInstance().getUser(authenticationToken.getUsername());
				List<Task> tasks = TaskDAO.getInstance().getUsersTasks(user.getId());
				for(Task task : tasks) {
					if(task.isResumable()) 
						return MatrixGenerationConfigurationDAO.getInstance().getMatrixGenerationConfigurationFromTask(task.getId());
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(AuthenticationToken authenticationToken, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				MatrixGenerationConfiguration configuration = 
						MatrixGenerationConfigurationDAO.getInstance().getMatrixGenerationConfigurationFromTask(task.getId());
				return configuration;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				MatrixGenerationConfiguration matrixGenerationConfiguration = getMatrixGenerationConfiguration(authenticationToken, task);
				MatrixGenerationConfigurationDAO.getInstance().remove(matrixGenerationConfiguration);
				TaskDAO.getInstance().removeTask(task);
				switch(task.getTaskStage().getTaskStageEnum()) {
				case INPUT:
					break;
				case LEARN_TERMS:
					ListenableFuture<LearnResult> learnFuture = activeLearnFutures.get(matrixGenerationConfiguration.getId());
					learnFuture.cancel(true);
					break;
				case OUTPUT:
					break;
				case PARSE_TEXT:
					ListenableFuture<ParseResult> parseFuture = activeParseFutures.get(matrixGenerationConfiguration.getId());
					parseFuture.cancel(true);
					break;
				case PREPROCESS_TEXT:
					break;
				case REVIEW_TERMS:
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
	

	/*@Override
	public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			return MatrixGenerationJobStatus.MARKUP;
		}
		return null;
	}

	@Override
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			
		}
	}*/

}
