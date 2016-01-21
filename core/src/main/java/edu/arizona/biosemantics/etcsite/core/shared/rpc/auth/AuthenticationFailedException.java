package edu.arizona.biosemantics.etcsite.core.shared.rpc.auth;

public class AuthenticationFailedException extends Exception {

	public AuthenticationFailedException(String message) {
		super(message);
	}
	
	public AuthenticationFailedException() {
	
	}
}
