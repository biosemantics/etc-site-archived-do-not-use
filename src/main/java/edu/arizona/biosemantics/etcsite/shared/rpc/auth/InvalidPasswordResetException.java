package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

public class InvalidPasswordResetException extends Exception {

	public InvalidPasswordResetException(String message) {
		super(message);
	}
	
	public InvalidPasswordResetException() {
	
	}
	
}
