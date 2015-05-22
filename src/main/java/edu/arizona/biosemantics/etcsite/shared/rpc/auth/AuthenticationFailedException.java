package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

public class AuthenticationFailedException extends Exception {

	public AuthenticationFailedException(String message) {
		super(message);
	}
	
	public AuthenticationFailedException() {
	
	}
}
