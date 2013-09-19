package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.file.CSVValidator;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLValidator;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileService fileService = new FileService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private CSVValidator csvValidator = new CSVValidator();
	private XMLValidator xmlValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
	
	@Override
	public boolean isValidTaxonDescription(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			String fileContent = fileAccessService.getFileContent(authenticationToken, target);
			return xmlValidator.validate(fileContent);
		} 
		return false;
	}

	@Override
	public boolean isValidGlossary(AuthenticationToken authenticationToken,
			String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			String fileContent = fileAccessService.getFileContent(authenticationToken, target);
			return csvValidator.validate(fileContent);
		} 
		return false;
	}

	@Override
	public boolean isValidEuler(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			String fileContent = fileAccessService.getFileContent(authenticationToken, target);
			return csvValidator.validate(fileContent);
		} 
		return false;
	}

}
