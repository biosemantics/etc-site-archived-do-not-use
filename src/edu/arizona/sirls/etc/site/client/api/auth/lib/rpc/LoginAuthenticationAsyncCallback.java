package edu.arizona.sirls.etc.site.client.api.auth.lib.rpc;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public class LoginAuthenticationAsyncCallback implements AsyncCallback<AuthenticationResult> {

	private Set<ILoginAuthenticationAsyncCallbackListener> listeners = new HashSet<ILoginAuthenticationAsyncCallbackListener>();
	
	@Override
	public void onFailure(Throwable caught) {
		for(ILoginAuthenticationAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}

	@Override
	public void onSuccess(AuthenticationResult result) {
		for(ILoginAuthenticationAsyncCallbackListener listener : listeners) 
			listener.notifyResult(result);
	}

	public void addListener(ILoginAuthenticationAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ILoginAuthenticationAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}
}
