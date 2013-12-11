package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.CSVValidator;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLValidator;

public class FileFormatService extends RemoteServiceServlet implements IFileFormatService {

	private IFileAccessService fileAccessService = new FileAccessService();
	private CSVValidator csvValidator = new CSVValidator();
	private XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
	private XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
	
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
}
