package edu.arizona.sirls.etc.site.client.api.auth;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public interface IAuthenticationClientListener {

	public void notifyAuthentication(AuthenticationResult authenticationResult);
	
}
