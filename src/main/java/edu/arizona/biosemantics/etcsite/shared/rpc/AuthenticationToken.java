package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class AuthenticationToken implements Serializable {

	private static final long serialVersionUID = 5502478871541626972L;
	protected String userId;
	protected String sessionID;

	public AuthenticationToken() { }
	
	public AuthenticationToken(String userId, String sessionID) { 
		this.userId = userId;
		this.sessionID = sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public void setUserId(String id){
		userId = id;
	}
	
	public String getUsername(){ // to maintain compatibility, even though username is actually uniqueid. _ags 
		return getUserId();
	}
}
