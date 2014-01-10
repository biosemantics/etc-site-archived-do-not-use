package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface IFileFormatServiceAsync {
	
	public void isValidTaxonDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidMarkedupTaxonDescriptionContent(AuthenticationToken authenticationToken, String content, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidMatrix(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);

	public void isValidGlossary(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
	public void isValidEuler(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);
	
}
