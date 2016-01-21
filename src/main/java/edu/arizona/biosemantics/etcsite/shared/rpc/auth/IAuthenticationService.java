package edu.arizona.biosemantics.etcsite.shared.rpc.auth;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.model.Captcha;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.CaptchaException;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.IncorrectCaptchaSolutionException;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.InvalidPasswordResetException;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.LoginGoogleResult;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.NoSuchUserException;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.OpenPasswordResetRequestException;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.RegistrationFailedException;

@RemoteServiceRelativePath("auth")
public interface IAuthenticationService extends RemoteService {

	public AuthenticationResult login(String user, String password);

	public LoginGoogleResult loginOrSignupWithGoogle(String googleAuthCode) throws RegistrationFailedException;

	public AuthenticationResult isValidSession(
			AuthenticationToken authenticationToken);

	public void requestPasswordResetCode(int captchaId, String captchaSolution,
			String email) throws IncorrectCaptchaSolutionException, NoSuchUserException, OpenPasswordResetRequestException;

	public void resetPassword(String email, String resetCode,
			String newPassword) throws NoSuchUserException, InvalidPasswordResetException;

	public Captcha createCaptcha();
	
	public AuthenticationResult signupUser(int captchaId, String captchaSolution,
			String firstName, String lastName, String email, String password) throws CaptchaException, RegistrationFailedException;

}
