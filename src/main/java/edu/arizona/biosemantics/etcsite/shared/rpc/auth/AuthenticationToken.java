package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

import java.io.Serializable;

public class AuthenticationToken implements Serializable {

	private static final long serialVersionUID = 5502478871541626972L;
	protected int userId;
	protected String sessionID;

	public AuthenticationToken() { }
	
	public AuthenticationToken(int userId, String sessionID) { 
		this.userId = userId;
		this.sessionID = sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	public int getUserId(){
		return userId;
	}
	
	public void setUserId(int id){
		userId = id;
	}
	
	@Override
	public String toString() {
		return "user id " + userId + ", session id " + sessionID;
	}
}
