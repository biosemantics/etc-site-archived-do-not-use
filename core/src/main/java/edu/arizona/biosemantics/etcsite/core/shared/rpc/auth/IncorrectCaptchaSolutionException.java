package edu.arizona.biosemantics.etcsite.core.shared.rpc.auth;

public class IncorrectCaptchaSolutionException extends Exception {

	public IncorrectCaptchaSolutionException(String message) {
		super(message);
	}
	
	public IncorrectCaptchaSolutionException() {
	
	}
	
}
