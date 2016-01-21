package edu.arizona.biosemantics.etcsite.core.shared.rpc.auth;

public class RegistrationFailedException extends Exception {

	public RegistrationFailedException() { }
	
	public RegistrationFailedException(String message) {
		super(message);
	}
	
}
