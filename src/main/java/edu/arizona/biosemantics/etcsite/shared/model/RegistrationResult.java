package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;

public class RegistrationResult implements Serializable {

	private static final long serialVersionUID = -3301088846498576341L;

	public static final String SUCCESS_MESSAGE = "Your account has been registered!";
	public static final String EMAIL_ALREADY_REGISTERED_MESSAGE = "This email address is already in use.";
	public static final String CAPTCHA_INCORRECT_MESSAGE = "The security code you entered was incorrect or expired. Please try again.";
	public static final String GENERIC_ERROR_MESSAGE = "An error occurred while processing your request. Please try again later.";
	
	private boolean result;
	private String message;

	public RegistrationResult() {}
	
	public RegistrationResult(boolean result, String message) {
		this.result = result;
		this.message = message;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
