package edu.arizona.sirls.etc.site.client.builder.handler;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public interface IAuthenticationValidatorListener {

	public void notify(AuthenticationResult authenticationResult);
	
}
