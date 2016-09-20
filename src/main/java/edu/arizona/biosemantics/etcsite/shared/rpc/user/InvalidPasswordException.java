package edu.arizona.biosemantics.etcsite.shared.rpc.user;

public class InvalidPasswordException extends Exception {

	public InvalidPasswordException() { }
	
	public InvalidPasswordException(String message) {
		super(message);
	}
	
}
