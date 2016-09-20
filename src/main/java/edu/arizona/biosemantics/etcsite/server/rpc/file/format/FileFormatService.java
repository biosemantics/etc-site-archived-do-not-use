package edu.arizona.biosemantics.etcsite.server.rpc.file.format;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.process.file.CSVValidator;
import edu.arizona.biosemantics.etcsite.server.process.file.XMLValidator;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlModelFileCreator;
import edu.arizona.biosemantics.etcsite.server.process.file.XmlNamespaceManager;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private Logger logger = Logger.getLogger(FileFormatService.class);
	
	private IUserService userService;
	private IFileAccessService fileAccessService;
	private CSVValidator csvValidator;
	private XMLValidator taxonDescriptionValidator;
	private XMLValidator markedUpTaxonDescriptionValidator;
	private XmlNamespaceManager xmlNamespaceManager;
	private XmlModelFileCreator xmlModelFileCreator;
	
	@Inject
	public FileFormatService(IUserService userService, IFileAccessService fileAccessService, CSVValidator csvValidator, 
			XmlNamespaceManager xmlNamespaceManager, @Named("TaxonDescription")XMLValidator taxonDescriptionValidator, 
			@Named("MarkedUpTaxonDescription")XMLValidator markedUpTaxonDescriptionValidator, 
			XmlModelFileCreator xmlModelFileCreator) {
		this.userService = userService;
		this.fileAccessService = fileAccessService;
		this.csvValidator = csvValidator;
		this.xmlNamespaceManager = xmlNamespaceManager;
		this.taxonDescriptionValidator = taxonDescriptionValidator;
		this.markedUpTaxonDescriptionValidator = markedUpTaxonDescriptionValidator;
		this.xmlModelFileCreator = xmlModelFileCreator;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public boolean isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath) 
			throws PermissionDeniedException, GetFileContentFailedException {
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		return taxonDescriptionValidator.validate(fileContent);
	}
	@Override
	public boolean isValidTaxonDescriptionContent(AuthenticationToken authenticationToken, String fileContent) {
		return taxonDescriptionValidator.validate(fileContent);
	}
	
	@Override
	public boolean isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {		
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		return markedUpTaxonDescriptionValidator.validate(fileContent);
	}
	
	public boolean isValidMatrix(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {		
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		return csvValidator.validate(fileContent);
	}
	
	@Override
	public boolean isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content) {
		return markedUpTaxonDescriptionValidator.validate(content);
	}

	@Override
	public boolean isValidGlossary(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {		
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		return csvValidator.validate(fileContent);
	}

	@Override
	public boolean isValidEuler(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {	
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		return csvValidator.validate(fileContent);
	}
	@Override
	public List<XmlModelFile> createTaxonDescriptionFile(AuthenticationToken authenticationToken, String text) {
		String operator = "N/A";
		try {
			operator = userService.getUser(authenticationToken).getFullNameEmailAffiliation();
		} catch (UserNotFoundException e) {
			log(LogLevel.ERROR, "Couldn't find user", e);
		}
		List<XmlModelFile> result = xmlModelFileCreator.createXmlModelFiles(text, operator);
		return result;
	}
	
	@Override
	public String getSchema(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		return xmlNamespaceManager.getSchema(fileContent);
	}
	
	@Override
	public String setSchema(AuthenticationToken token, String content, FileTypeEnum fileTypeEnum) {
		return xmlNamespaceManager.setXmlSchema(content, fileTypeEnum);
	}
	
	@Override
	public boolean isValidXmlContentForSchema(AuthenticationToken token, String content, String xmlSchema) {
		String[] localSchemaFiles = { Configuration.taxonDescriptionSchemaFile, Configuration.markedUpTaxonDescriptionSchemaFile };
		for(String localSchemaFile : localSchemaFiles) {
			File file = new File(localSchemaFile);
			String filename = file.getName();
			if(xmlSchema.endsWith(filename)) {
				XMLValidator validator = new XMLValidator(file);
				return validator.validate(content);
			}
		}
		XMLValidator validator = null;
		try {
			validator = new XMLValidator(new URL(xmlSchema));
		} catch (MalformedURLException e) {
			log(LogLevel.ERROR, "Couldn't parse URL", e);
		}
		if(validator != null)
			return validator.validate(content);
		return false;
	}
}
