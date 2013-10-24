package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.file.CSVValidator;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLValidator;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private CSVValidator csvValidator = new CSVValidator();
	private XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
	private XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
	
	@Override
	public boolean isValidTaxonDescription(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			String fileContent = fileAccessService.getFileContent(authenticationToken, target);
			return taxonDescriptionValidator.validate(fileContent);
		} 
		return false;
	}
	
	@Override
	public boolean isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String target) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			String fileContent = fileAccessService.getFileContent(authenticationToken, target);
			return markedUpTaxonDescriptionValidator.validate(fileContent);
		} 
		return false;
	}
	
	@Override
	public boolean isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			return markedUpTaxonDescriptionValidator.validate(content);
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
