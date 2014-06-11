package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.db.User;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("auth")
public interface IAuthenticationService extends RemoteService {
	
	public RPCResult<AuthenticationResult> login(String user, String password);

	RPCResult<RegistrationResult> registerAccount(int captchaId,
			String captchaSolution, String firstName, String lastName,
			String email, String password);
	
	public RPCResult<User> getUser(AuthenticationToken authenticationToken);
	
	public RPCResult<AuthenticationResult> isValidSession(AuthenticationToken authenticationToken);

	RPCResult<UpdateUserResult> updateUser(
			AuthenticationToken authenticationToken, String oldPassword,
			String firstName, String lastName, String email,
			String newPassword, String affiliation, String bioportalUserId,
			String bioportalAPIKey);

	RPCResult<PasswordResetResult> requestPasswordResetCode(int captchaId,
			String captchaSolution, String openIdProviderId);

	RPCResult<PasswordResetResult> requestPasswordReset(String openIdProviderId,
			String code, String newPassword);

	RPCResult<RequestCaptchaResult> requestCaptcha();
	
	RPCResult<String> getOperator(AuthenticationToken authenticationToken);

}
