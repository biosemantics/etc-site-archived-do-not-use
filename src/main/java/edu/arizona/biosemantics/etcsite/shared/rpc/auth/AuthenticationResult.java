package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

import java.io.Serializable;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;


public class AuthenticationResult implements Serializable {

	private static final long serialVersionUID = -3162151093040677272L;
	private boolean result;
	private String sessionId;
	private ShortUser user;
	
	public AuthenticationResult() {}
	
	public AuthenticationResult(boolean result, String sessionId, ShortUser user) {
		this.result = result;
		this.sessionId = sessionId;
		this.user = user;
	}
	
	public String getSessionID() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}

	public ShortUser getUser() {
		return user;
	}
	
}


