package edu.arizona.biosemantics.etcsite.core.shared.rpc.auth;

public class NoSuchUserException extends Exception {

	public NoSuchUserException(String message) {
		super(message);
	}
	
	public NoSuchUserException() {
	
	}
	
}
