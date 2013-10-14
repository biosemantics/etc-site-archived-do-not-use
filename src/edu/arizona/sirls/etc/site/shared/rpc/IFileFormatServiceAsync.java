package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

public interface IFileFormatServiceAsync {
	
	public void isValidTaxonDescription(AuthenticationToken authenticationToken, String target, AsyncCallback<Boolean> callback);

	public void isValidGlossary(AuthenticationToken authenticationToken, String target, AsyncCallback<Boolean> callback);
	
	public void isValidEuler(AuthenticationToken authenticationToken, String target, AsyncCallback<Boolean> callback);
	
	public void isValidMarkedupTaxonDescription(AuthenticationToken authenticationToken, String target, AsyncCallback<Boolean> callback);
}
