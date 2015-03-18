package edu.arizona.biosemantics.etcsite.shared.rpc.user;

public class UserNotFoundException extends Exception {

	public UserNotFoundException() { }
	
	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(Exception e) {
		super(e);
	}
	
}
