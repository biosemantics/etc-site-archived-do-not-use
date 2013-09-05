package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("auth")
public interface IAuthenticationService extends RemoteService {
	
	public AuthenticationResult login(String user, String password);
	
	public AuthenticationResult isValidSession(AuthenticationToken authenticationToken);

}
