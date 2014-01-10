package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class AuthenticationToken implements Serializable {

	private static final long serialVersionUID = 5502478871541626972L;
	protected String username;
	protected String sessionID;

	public AuthenticationToken() { }
	
	public AuthenticationToken(String username, String sessionID) { 
		this.username = username;
		this.sessionID = sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
