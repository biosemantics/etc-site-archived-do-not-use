package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.db.User;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface IAuthenticationServiceAsync {
	
	public void login(String user, String password,
			AsyncCallback<RPCResult<AuthenticationResult>> callback);

	void registerAccount(int captchaId, String captchaSolution,
			String firstName, String lastName, String email, String password,
			AsyncCallback<RPCResult<RegistrationResult>> callback);
	
	void getUser(AuthenticationToken authenticationToken,
			AsyncCallback<RPCResult<User>> callback);

	public void isValidSession(AuthenticationToken authentication,
			AsyncCallback<RPCResult<AuthenticationResult>> callback);

	void updateUser(AuthenticationToken authenticationToken,
			String oldPassword, String firstName, String lastName,
			String email, String newPassword, String affiliation,
			String bioportalUserId, String bioportalAPIKey,
			AsyncCallback<RPCResult<UpdateUserResult>> callback);

	void requestPasswordResetCode(int captchaId, String captchaSolution,
			String openIdProviderId,
			AsyncCallback<RPCResult<PasswordResetResult>> callback);

	void requestPasswordReset(String openIdProviderId, String code,
			String newPassword,
			AsyncCallback<RPCResult<PasswordResetResult>> callback);

	public void requestCaptcha(AsyncCallback<RPCResult<RequestCaptchaResult>> callback);

	public void getOperator(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<String>> callback);
	
}
