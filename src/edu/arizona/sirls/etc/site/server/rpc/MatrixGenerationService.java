package edu.arizona.sirls.etc.site.server.rpc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
import edu.arizona.sirls.etc.site.shared.rpc.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.file.BracketValidator;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLFileFormatter;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private XMLFileFormatter xmlFileFormatter = new XMLFileFormatter();
	private BracketValidator bracketValidator = new BracketValidator();

	@Override
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			return new LearnInvocation(5989, 23212);
		}
		return null;
	}

	@Override
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken,
			MatrixGenerationJob matrixGenerationJob) {
		List<PreprocessedDescription> result = new LinkedList<PreprocessedDescription>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			//do preprocessing here, return result immediately or always only return an invocation
			//and make user come back when ready?
			String inputDirectory = matrixGenerationJob.getTaxonDescriptionFile();
			
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
	public boolean outputResult(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult()) {
			result = fileService.createFile(authenticationToken, matrixGenerationJob.getOutputFile());
		}
		return result;
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
