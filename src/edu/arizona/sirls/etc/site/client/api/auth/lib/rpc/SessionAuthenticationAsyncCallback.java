package edu.arizona.sirls.etc.site.client.api.auth.lib.rpc;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public class SessionAuthenticationAsyncCallback implements AsyncCallback<AuthenticationResult> {

	private Set<ISessionAuthenticationAsyncCallbackListener> listeners = new HashSet<ISessionAuthenticationAsyncCallbackListener>();
	
	@Override
	public void onFailure(Throwable caught) {
		for(ISessionAuthenticationAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}

	@Override
	public void onSuccess(AuthenticationResult authenticationResult) {
		for(ISessionAuthenticationAsyncCallbackListener listener : listeners) 
			listener.notifyResult(authenticationResult, this);
	}

	public void addListener(ISessionAuthenticationAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ISessionAuthenticationAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}
}
