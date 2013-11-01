package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

@RemoteServiceRelativePath("fileFormat")
public interface IFileFormatService extends RemoteService {

	public RPCResult<Boolean> isValidTaxonDescription(AuthenticationToken authenticationToken, String target);
	
	public RPCResult<Boolean> isValidGlossary(AuthenticationToken authenticationToken, String target);

	public RPCResult<Boolean> isValidEuler(AuthenticationToken authenticationToken, String target);

	public RPCResult<Boolean> isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String target);
	
	public RPCResult<Boolean> isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content);
}
