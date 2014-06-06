package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class PasswordResetResult implements Serializable {

	private static final long serialVersionUID = -3302088846798576341L;

	public static final String EMAIL_SENT_MESSAGE = "An authentication code will be sent to your primary email address (<email>). ";
	public static final String ANTI_SPAM_MESSAGE = "You must wait until a minute has passed before requesting another authentication code.";
	public static final String NO_SUCH_USER_MESSAGE = "There is no account associated with this email address. Please re-enter.";
	public static final String GENERIC_ERROR_MESSAGE = "An error occurred while processing your request. Please try again later.";
	public static final String INCORRECT_OR_EXPIRED_MESSAGE = "This code is invalid or has expired. Try entering the code again or sending a new one.";
	public static final String PASSWORD_RESET_MESSAGE = "Your password has been reset.";
	
	private boolean result;
	private String message;

	public PasswordResetResult() {}
	
	public PasswordResetResult(boolean result, String message) {
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
