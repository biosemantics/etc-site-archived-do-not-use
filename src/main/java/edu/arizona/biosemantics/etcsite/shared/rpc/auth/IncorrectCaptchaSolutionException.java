package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

public class IncorrectCaptchaSolutionException extends Exception {

	public IncorrectCaptchaSolutionException(String message) {
		super(message);
	}
	
	public IncorrectCaptchaSolutionException() {
	
	}
	
}
