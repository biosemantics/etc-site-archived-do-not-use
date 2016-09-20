package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

public class NoSuchUserException extends Exception {

	public NoSuchUserException(String message) {
		super(message);
	}
	
	public NoSuchUserException() {
	
	}
	
}
