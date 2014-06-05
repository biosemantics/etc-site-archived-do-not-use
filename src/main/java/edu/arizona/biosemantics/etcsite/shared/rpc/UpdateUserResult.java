package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class UpdateUserResult implements Serializable {

	private static final long serialVersionUID = -3302088846498576341L;

	public static final String SUCCESS_MESSAGE = "Your account has been updated.";
	public static final String INVALID_SESSION_MESSAGE = "Your session has expired. Please sign in again.";
	public static final String INCORRECT_PASSWORD_MESSAGE = "The password you entered is incorrect."; 
	public static final String GENERIC_ERROR_MESSAGE = "An error occurred while processing your request. Please try again later.";
	
	private boolean result;
	private String message;
	private String newSessionId;

	public UpdateUserResult() {}
	
	public UpdateUserResult(boolean result, String message, String newSessionId) {
		this.result = result;
		this.message = message;
		this.newSessionId = newSessionId;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getNewSessionId() {
		return newSessionId;
	}

	public void setNewSessionId(String newSessionId) {
		this.newSessionId = newSessionId;
	}
}
