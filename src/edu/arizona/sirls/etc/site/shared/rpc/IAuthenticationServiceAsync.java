package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface IAuthenticationServiceAsync {
	
	public void login(String user, String password, AsyncCallback<RPCResult<AuthenticationResult>> callback);
	
	public void isValidSession(AuthenticationToken authentication, AsyncCallback<RPCResult<AuthenticationResult>> callback);
	
}
