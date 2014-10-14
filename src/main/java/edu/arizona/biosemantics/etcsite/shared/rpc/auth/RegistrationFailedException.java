package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

public class RegistrationFailedException extends Exception {

	private String message = "";

	public RegistrationFailedException() { }
	
	public RegistrationFailedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
}
