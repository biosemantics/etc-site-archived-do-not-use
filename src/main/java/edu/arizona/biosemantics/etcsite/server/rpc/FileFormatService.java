package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.shared.file.CSVValidator;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.XMLValidator;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private Logger logger = Logger.getLogger(FileFormatService.class);
	
	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private CSVValidator csvValidator = new CSVValidator();
	private XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
	private XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
	private XmlModelFileCreator xmlModelFileCreator = new XmlModelFileCreator();
	private XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
	
	@Override
	public RPCResult<Boolean> isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, taxonDescriptionValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
	@Override
	public RPCResult<Boolean> isValidTaxonDescriptionContent(AuthenticationToken authenticationToken, String fileContent) {
			return new RPCResult<Boolean>(true, taxonDescriptionValidator.validate(fileContent));
	}
	
	
	@Override
	public RPCResult<Boolean> isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath) {		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, markedUpTaxonDescriptionValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
	
	public RPCResult<Boolean> isValidMatrix(AuthenticationToken authenticationToken, String filePath) {		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, csvValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
	
	@Override
	public RPCResult<Boolean> isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content) {
		return new RPCResult<Boolean>(true, markedUpTaxonDescriptionValidator.validate(content));
	}

	@Override
	public RPCResult<Boolean> isValidGlossary(AuthenticationToken authenticationToken, String filePath) {		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, csvValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}

	@Override
	public RPCResult<Boolean> isValidEuler(AuthenticationToken authenticationToken, String filePath) {	
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, csvValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
	@Override
	public RPCResult<List<XmlModelFile>> createTaxonDescriptionFile(AuthenticationToken authenticationToken, String text) {
		RPCResult<String> operatorResult = authenticationService.getOperator(authenticationToken);
		if(!operatorResult.isSucceeded())
			return new RPCResult<List<XmlModelFile>>(false, operatorResult.getMessage());
		logger.debug("text: " + text);
		List<XmlModelFile> result = xmlModelFileCreator.createXmlModelFiles(text, operatorResult.getData());
		return new RPCResult<List<XmlModelFile>>(true, result);
	}
	
	@Override
	public RPCResult<String> getSchema(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, filePath);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			String schema = null;
			try {
				schema = xmlNamespaceManager.getSchema(fileContent);
			} catch(Exception e) {
				e.printStackTrace();
				return new RPCResult<String>(false, "Failed to read schema", "");
			}
			if(schema != null)
				return new RPCResult<String>(true, "", schema);
			else
				return new RPCResult<String>(false, "No schema available", "");
		}
		return new RPCResult<String>(false, fileContentResult.getMessage(), "");
	}
	
	@Override
	public RPCResult<String> setSchema(AuthenticationToken token, String content, FileTypeEnum fileTypeEnum) {
		try {
			String result = xmlNamespaceManager.setXmlSchema(content, fileTypeEnum);
			return new RPCResult<String>(true, "", result);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Failed to set schema in file", "");
		}
	}
	
	@Override
	public RPCResult<Boolean> isValidXmlContentForSchema(AuthenticationToken token, String content, String xmlSchema) {
		String[] localSchemaFiles = { Configuration.taxonDescriptionSchemaFile, Configuration.markedUpTaxonDescriptionSchemaFile };
		for(String localSchemaFile : localSchemaFiles) {
			File file = new File(localSchemaFile);
			String filename = file.getName();
			if(xmlSchema.endsWith(filename)) {
				XMLValidator validator = new XMLValidator(file);
				boolean valid = validator.validate(content);
				return new RPCResult<Boolean>(true, valid);
			}
		}
		try {
			XMLValidator validator = new XMLValidator(new URL(xmlSchema));
			boolean valid = validator.validate(content);
			return new RPCResult<Boolean>(true, valid);
		} catch(Exception e) {
			return new RPCResult<Boolean>(false, "Couldn't find the schema file");
		}
	}
}
