package edu.arizona.sirls.etc.site.client.api.auth.lib.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public interface ILoginAuthenticationAsyncCallbackListener {
	
	public void notifyException(Throwable t);
	
	public void notifyResult(AuthenticationResult result);
}
