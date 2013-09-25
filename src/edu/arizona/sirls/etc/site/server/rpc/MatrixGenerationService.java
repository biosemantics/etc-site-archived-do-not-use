package edu.arizona.sirls.etc.site.server.rpc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
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

	@Override
	public MatrixGenerationConfiguration start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName) {
		try {
			edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum taskType = edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION;
			TaskType dbTaskType = TaskTypeDAO.getInstance().getTaskType(taskType.toString());
			TaskStage taskStage = TaskStageDAO.getInstance().getTaskStage(dbTaskType, TaskStageEnum.INPUT.toString());
			User user = UserDAO.getInstance().getUser(authenticationToken.getUsername());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			taskService.addTask(authenticationToken, task);
			
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
				TaskDAO.getInstance().updateTask(matrixGenerationConfiguration.getTask().getId(), 
						edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION, 
						TaskStageEnum.PREPROCESS_TEXT);
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
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			try {
				TaskDAO.getInstance().updateTask(matrixGenerationConfiguration.getTask().getId(), 
						edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION, 
						TaskStageEnum.LEARN_TERMS);
				return new LearnInvocation(5989, 23212);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String review(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				TaskDAO.getInstance().updateTask(matrixGenerationConfiguration.getTask().getId(), 
						edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION, 
						TaskStageEnum.REVIEW_TERMS);
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
	public String parse(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			try {
				TaskDAO.getInstance().updateTask(matrixGenerationConfiguration.getTask().getId(), 
						edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION, 
						TaskStageEnum.PARSE_TEXT);
				return "";
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
				TaskDAO.getInstance().updateTask(matrixGenerationConfiguration.getTask().getId(), 
						edu.arizona.sirls.etc.site.shared.rpc.TaskTypeEnum.MATRIX_GENERATION, 
						TaskStageEnum.OUTPUT);

				// TODO: create a directory parallel to input directory with name "input dir name" + _MGResult.. with result files inside
				//result = fileService.createFile(authenticationToken, matrixGenerationJob..getOutputFile());
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(AuthenticationToken authenticationToken, Task task) {
		// TODO Auto-generated method stub
		return null;
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
