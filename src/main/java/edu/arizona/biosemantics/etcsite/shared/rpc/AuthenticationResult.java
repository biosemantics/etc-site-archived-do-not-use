package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class AuthenticationResult implements Serializable {

	private static final long serialVersionUID = -3162151093040677272L;
	private boolean result;
	private String sessionID;
	private String userId;
	
	public AuthenticationResult() {}
	
	public AuthenticationResult(boolean result, String sessionID, String userId) {
		this.result = result;
		this.sessionID = sessionID;
		this.userId = userId;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String user) {
		this.userId = user;
	}
	
}
