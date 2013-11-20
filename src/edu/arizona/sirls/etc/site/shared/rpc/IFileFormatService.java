package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("fileFormat")
public interface IFileFormatService extends RemoteService {

	public RPCResult<Boolean> isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isValidMatrix(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Boolean> isValidGlossary(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Boolean> isValidEuler(AuthenticationToken authenticationToken, String filePath);

	public RPCResult<Boolean> isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content);
}
