package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("auth")
public interface IAuthenticationService extends RemoteService {
	
	public RPCResult<AuthenticationResult> login(String user, String password);
	
	public RPCResult<AuthenticationResult> isValidSession(AuthenticationToken authenticationToken);

}
