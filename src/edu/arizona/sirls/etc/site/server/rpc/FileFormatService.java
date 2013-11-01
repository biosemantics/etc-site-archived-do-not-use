package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.CSVValidator;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLValidator;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private CSVValidator csvValidator = new CSVValidator();
	private XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
	private XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
	
	@Override
	public RPCResult<Boolean> isValidTaxonDescription(AuthenticationToken authenticationToken, String target) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		if(target.trim().isEmpty()) 
			return new RPCResult<Boolean>(false, "Target may not be empty");
		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, target);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, taxonDescriptionValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
	
	@Override
	public RPCResult<Boolean> isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String target) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		if(target.trim().isEmpty()) 
			return new RPCResult<Boolean>(false, "Target may not be empty");
		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, target);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, markedUpTaxonDescriptionValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
	
	@Override
	public RPCResult<Boolean> isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		
		if(authenticationService.isValidSession(authenticationToken).getData().getResult()) { 
			return new RPCResult<Boolean>(true, markedUpTaxonDescriptionValidator.validate(content));
		} 
		return new RPCResult<Boolean>(false, "Authentication failed");
	}

	@Override
	public RPCResult<Boolean> isValidGlossary(AuthenticationToken authenticationToken,
			String target) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		if(target.trim().isEmpty()) 
			return new RPCResult<Boolean>(false, "Target may not be empty");
		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, target);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, csvValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}

	@Override
	public RPCResult<Boolean> isValidEuler(AuthenticationToken authenticationToken, String target) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<Boolean>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<Boolean>(false, "Authentication failed");
		if(target.trim().isEmpty()) 
			return new RPCResult<Boolean>(false, "Target may not be empty");
		
		RPCResult<String> fileContentResult = fileAccessService.getFileContent(authenticationToken, target);
		if(fileContentResult.isSucceeded()) {
			String fileContent = fileContentResult.getData();
			return new RPCResult<Boolean>(true, csvValidator.validate(fileContent));
		}
		return new RPCResult<Boolean>(false, fileContentResult.getMessage());
	}
}
