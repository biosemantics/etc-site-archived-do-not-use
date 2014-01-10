package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface IAuthenticationServiceAsync {
	
	public void login(String user, String password, AsyncCallback<RPCResult<AuthenticationResult>> callback);
	
	public void isValidSession(AuthenticationToken authentication, AsyncCallback<RPCResult<AuthenticationResult>> callback);
	
}
