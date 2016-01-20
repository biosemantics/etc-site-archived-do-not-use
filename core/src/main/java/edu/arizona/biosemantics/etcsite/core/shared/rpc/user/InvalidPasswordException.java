package edu.arizona.biosemantics.etcsite.core.shared.rpc.user;

public class InvalidPasswordException extends Exception {

	public InvalidPasswordException() { }
	
	public InvalidPasswordException(String message) {
		super(message);
	}
	
}
