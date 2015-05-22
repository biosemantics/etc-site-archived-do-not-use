package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

import java.io.Serializable;

public class LoginGoogleResult implements Serializable {

	private AuthenticationResult authenticationResult;
	private boolean newlyRegistered = false;
	
	public LoginGoogleResult() { }
	
	public LoginGoogleResult(AuthenticationResult authenticationResult,
			boolean newlyRegistered) {
		super();
		this.authenticationResult = authenticationResult;
		this.newlyRegistered = newlyRegistered;
	}

	public AuthenticationResult getAuthenticationResult() {
		return authenticationResult;
	}

	public void setAuthenticationResult(AuthenticationResult authenticationResult) {
		this.authenticationResult = authenticationResult;
	}

	public boolean isNewlyRegistered() {
		return newlyRegistered;
	}

	public void setNewlyRegistered(boolean newlyRegistered) {
		this.newlyRegistered = newlyRegistered;
	}
	
	
	
}
