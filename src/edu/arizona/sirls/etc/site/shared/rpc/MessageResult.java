package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;

public class MessageResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean succeeded;
	private String message;
	
	public MessageResult() { }
	
	public MessageResult(boolean succeeded, String message) {
		super();
		this.succeeded = succeeded;
		this.message = message;
	}
	
	public MessageResult(boolean succeeded) {
		this.succeeded = succeeded;
		this.message = "";
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	public String getMessae() {
		return message;
	}

	public void setMessae(String messae) {
		this.message = messae;
	}
	
	
	
	
}
