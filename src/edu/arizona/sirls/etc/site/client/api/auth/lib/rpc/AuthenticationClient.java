package edu.arizona.sirls.etc.site.client.api.auth.lib.rpc;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.api.auth.IAuthenticationClient;
import edu.arizona.sirls.etc.site.client.api.auth.IAuthenticationClientListener;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

public class AuthenticationClient implements IAuthenticationClient, ILoginAuthenticationAsyncCallbackListener, ISessionAuthenticationAsyncCallbackListener {
	
	private final IAuthenticationServiceAsync authenticationService = GWT.create(IAuthenticationService.class);
	private Set<IAuthenticationClientListener> listeners = new HashSet<IAuthenticationClientListener>();

	private static AuthenticationClient instance;
	
	public static AuthenticationClient getInstance() {
		if(instance == null)
			instance = new AuthenticationClient();
		return instance;
	}
	
	private AuthenticationClient() { } 
	
	public void login(String userName, String password) {
		LoginAuthenticationAsyncCallback loginAuthenticationAsyncCallback = new LoginAuthenticationAsyncCallback();
		loginAuthenticationAsyncCallback.addListener(this);
		authenticationService.login(userName, password, loginAuthenticationAsyncCallback);
	}

	@Override
	public void isValidSession(AuthenticationToken authentication) {
		SessionAuthenticationAsyncCallback sessionAuthenticationAsyncCallback = new SessionAuthenticationAsyncCallback();
		sessionAuthenticationAsyncCallback.addListener(this);
		authenticationService.isValidSession(authentication, sessionAuthenticationAsyncCallback);
	}
	
	@Override
	public void notifyException(Throwable t) {
		for(IAuthenticationClientListener listener : listeners)
			listener.notifyAuthentication(null);
	}

	@Override
	public void notifyResult(AuthenticationResult authenticationResult) {
		for(IAuthenticationClientListener listener : listeners) 
			listener.notifyAuthentication(authenticationResult);
	}
	
	@Override
	public void notifyResult(AuthenticationResult authenticationResult, SessionAuthenticationAsyncCallback callback) {
		for(IAuthenticationClientListener listener : listeners) 
			listener.notifyAuthentication(authenticationResult);
	}
		
	
	public void addListener(IAuthenticationClientListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IAuthenticationClientListener listener) {
		listeners.remove(listener);
	}


}
