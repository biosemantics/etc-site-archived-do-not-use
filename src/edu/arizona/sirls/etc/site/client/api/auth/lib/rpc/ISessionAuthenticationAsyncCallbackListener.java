package edu.arizona.sirls.etc.site.client.api.auth.lib.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public interface ISessionAuthenticationAsyncCallbackListener {
	
	public void notifyException(Throwable t);

	void notifyResult(AuthenticationResult authenticationResult, SessionAuthenticationAsyncCallback callback);
}
