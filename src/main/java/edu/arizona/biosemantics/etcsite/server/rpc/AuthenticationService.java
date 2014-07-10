package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.server.CaptchaManager;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.EmailManager;
import edu.arizona.biosemantics.etcsite.shared.db.PasswordResetRequest;
import edu.arizona.biosemantics.etcsite.shared.db.PasswordResetRequestDAO;
import edu.arizona.biosemantics.etcsite.shared.db.User;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.BCrypt;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.PasswordResetResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.RegistrationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.RequestCaptchaResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.UpdateUserResult;

/**
 * The server side implementation of the RPC service.
 */
public class AuthenticationService extends RemoteServiceServlet implements IAuthenticationService {

	private static final long serialVersionUID = 5337745818086392785L;
	
	private static int RESET_PASSWORD_MINIMUM_WAIT_TIME_SECONDS = 60;
	private static int RESET_PASSWORD_HOURS_BEFORE_EXPIRE = 1;
	
	private SecretKey key;
	private Cipher encryptCipher;
	private Cipher decryptCipher;
	
	public AuthenticationService(){
		try {
			key = KeyGenerator.getInstance("DES").generateKey();
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public RPCResult<AuthenticationResult> login(String email, String password) {
		try {
			User user = UserDAO.getInstance().getUser(email, "none");
			if(user != null && BCrypt.checkpw(password, user.getPassword())) {
				String sessionId = generateSessionId(user.getId(), user.getPassword());
				return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, sessionId, user));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<AuthenticationResult>(false, "Internal Server Error");
		}			
		return new RPCResult<AuthenticationResult>(true, "Authentication failed", new AuthenticationResult(false, null, null));
	}
	
	@Override
	public RPCResult<AuthenticationResult> loginWithGoogle(String accessToken) {

		try {
			URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			//int responseCode = connection.getResponseCode();
			
			//System.out.println("Sending GET request to " + url.getPath());
			//System.out.println("Response code: " + responseCode);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			
			while ((line = reader.readLine()) != null){
				response.append(line);
			}
			reader.close();
			
			//System.out.println("Got the result: \n" + response.toString());
			
			JSONObject elements = new JSONObject(response.toString());
			String firstName = elements.getString("given_name");
			String lastName = elements.getString("family_name");
			String openIdProviderId = elements.getString("email");
			
			//System.out.println("First name: " + firstName + ". Last name: " + lastName + ". Email: " + email + ".");
			
			//create an account for this user if they do not have one yet.	
			String dummyPassword = firstName + lastName;
			String encryptedDummyPassword = BCrypt.hashpw(dummyPassword, BCrypt.gensalt()); //encrypt password
			UserDAO.getInstance().addUser(openIdProviderId, encryptedDummyPassword, firstName, lastName, "google");
			
			
			User user = UserDAO.getInstance().getUser(openIdProviderId, "google");
			
			//update the user's first name and last name in case they changed them in their google account. (Dummy password will have changed too.)
			UserDAO.getInstance().updateUser(user.getId(), firstName, lastName, 
					user.getEmail(), encryptedDummyPassword, user.getAffiliation(), user.getBioportalUserId(), user.getBioportalAPIKey());
			user = UserDAO.getInstance().getUser(openIdProviderId, "google"); //refresh the user after update.
			
			String sessionId = generateSessionId(user.getId(), user.getPassword());
			return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, sessionId, user));
			
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<AuthenticationResult>(true, "Authentication failed", new AuthenticationResult(false, null, null));
		} 
	}
	
	@Override
	public RPCResult<RegistrationResult> registerLocalAccount(int captchaId, String captchaSolution, String firstName, String lastName, String email, String password) {
		
		//check to see if the captcha is correct.
		if (!CaptchaManager.getInstance().isValidSolution(captchaId, captchaSolution)){
			return new RPCResult<RegistrationResult>(true, new RegistrationResult(false, RegistrationResult.CAPTCHA_INCORRECT_MESSAGE));
		}
		
		String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		try {
			
			if (UserDAO.getInstance().addUser(email, encryptedPassword, firstName, lastName, "none")) //registration was successful.
				return new RPCResult<RegistrationResult>(true, new RegistrationResult(true, RegistrationResult.SUCCESS_MESSAGE));
			else 
				return new RPCResult<RegistrationResult>(true, new RegistrationResult(false, RegistrationResult.EMAIL_ALREADY_REGISTERED_MESSAGE));
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<RegistrationResult>(true, new RegistrationResult(false, RegistrationResult.GENERIC_ERROR_MESSAGE));
		}
	}
	
	
	@Override
	public RPCResult<UpdateUserResult> updateUser(AuthenticationToken authenticationToken, String oldPassword, String firstName, String lastName, String email, String newPassword, String affiliation, String bioportalUserId, String bioportalAPIKey) {
		
		int userId = authenticationToken.getUserId();
		
		//check to see if this is a valid session.
		if (!isValidSession(authenticationToken).getData().getResult()){
			System.err.println("Attempt made to update User data with an invalid session: " + authenticationToken.getSessionID());
			return new RPCResult<UpdateUserResult>(true, new UpdateUserResult(false, UpdateUserResult.INVALID_SESSION_MESSAGE, null));
		}
		
		try{ 
			//check to see if the provided password matches the logged-in user's password. 
			User user = UserDAO.getInstance().getUser(userId);
			if(user == null || !BCrypt.checkpw(oldPassword, user.getPassword())) {
				return new RPCResult<UpdateUserResult>(true, new UpdateUserResult(false, UpdateUserResult.INCORRECT_PASSWORD_MESSAGE, null));
			}
			
			//encrypt password
			String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
			
			//update the user with the new values. 
			if (UserDAO.getInstance().updateUser(userId, firstName, lastName, email, encryptedPassword, affiliation, bioportalUserId, bioportalAPIKey)){
				user = UserDAO.getInstance().getUser(userId);
				String newSessionId = generateSessionId(userId, user.getPassword());
				return new RPCResult<UpdateUserResult>(true, new UpdateUserResult(true, UpdateUserResult.SUCCESS_MESSAGE, newSessionId));
			}
			else //there is no user with that id. 
				return new RPCResult<UpdateUserResult>(true, new UpdateUserResult(false, UpdateUserResult.GENERIC_ERROR_MESSAGE, null));

		} catch (Exception e){ //something went wrong during user update.
			e.printStackTrace();
			return new RPCResult<UpdateUserResult>(true, new UpdateUserResult(false, UpdateUserResult.GENERIC_ERROR_MESSAGE, null));
		}
	}
	
	
	@Override
	public RPCResult<User> getUser(AuthenticationToken authenticationToken) {
		int userId = authenticationToken.getUserId();
		try {
			User user = UserDAO.getInstance().getUser(userId);
			
			if (user != null){
				return new RPCResult<User>(true, user);
			} else { //no such user exists.
				return new RPCResult<User>(false, "Tried to get user id " + userId + ", but no such user exists.", null);
			}
		} catch (Exception e){
			return new RPCResult<User>(false, "Error in getUser, passed id " + userId + ".", null);
		}
	}
	
	private String generateSessionId(int userId, String encryptedPassword){
		try {
			String sessionId = userId + ":" + encryptedPassword;
			return DatatypeConverter.printBase64Binary(sessionId.getBytes());
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public RPCResult<AuthenticationResult> isValidSession(AuthenticationToken authenticationToken) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, "admin", new User()));
		String sessionID = authenticationToken.getSessionID();
		if(sessionID != null){
			try {
				String completeString = new String(DatatypeConverter.parseBase64Binary(sessionID));
				int division = completeString.indexOf(':');
				int userId = Integer.parseInt(completeString.substring(0, division));
				String password = completeString.substring(division + 1, completeString.length());
				
				User user = UserDAO.getInstance().getUser(userId);
				if (user != null && user.getPassword().equals(password))
					return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, sessionID, user));
				else
					System.err.println("An invalid session was found with sessionID: " + sessionID);
			} catch (Exception e){
				e.printStackTrace();
				System.err.println("An error occurred while validating a sessionID. (Perhaps the server was restarted?)");
			}
		}
		return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(false, null, null));
	}
	
	
	@Override
	public RPCResult<PasswordResetResult> requestPasswordResetCode(int captchaId, String captchaSolution, String openIdProviderId) {
		//check to see if the captcha is correct.
		if (!CaptchaManager.getInstance().isValidSolution(captchaId, captchaSolution)){
			return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, RegistrationResult.CAPTCHA_INCORRECT_MESSAGE));
		}
		
		try {
			User user = UserDAO.getInstance().getUser(openIdProviderId, "none");
			if (user == null)
				return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.NO_SUCH_USER_MESSAGE));
			int userId = user.getId();
			String email = user.getEmail();
			
			PasswordResetRequest oldRequest = PasswordResetRequestDAO.getInstance().getRequest(userId);
			if (oldRequest != null){
				//see if it has been a long enough time since the last request to process this. 
				if ((new Date()).getTime() - oldRequest.getRequestTime().getTime() < RESET_PASSWORD_MINIMUM_WAIT_TIME_SECONDS * 1000)
					return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.ANTI_SPAM_MESSAGE));
				
				//delete the old request.
				PasswordResetRequestDAO.getInstance().removeRequests(userId);
			}
			
			//generate a code.
			String authenticationCode = generatePasswordResetCode();
			
			//add the new request to the database. 
			PasswordResetRequestDAO.getInstance().addRequest(userId, authenticationCode);
			
			//send the user an email. 
			String expireTime = RESET_PASSWORD_HOURS_BEFORE_EXPIRE + (RESET_PASSWORD_HOURS_BEFORE_EXPIRE == 1 ? " hour" : " hours");
			String bodyText = Configuration.passwordResetBody.replace("<openidproviderid>", openIdProviderId).replace("<code>", authenticationCode).replace("<expire>", expireTime);
			EmailManager.getInstance().sendEmail(email, Configuration.passwordResetSubject, bodyText);
			
			return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(true, PasswordResetResult.EMAIL_SENT_MESSAGE.replace("<email>", email)));
				
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.GENERIC_ERROR_MESSAGE));
		}
	}
	
	
	@Override
	public RPCResult<PasswordResetResult> requestPasswordReset(String openIdProviderId, String code, String newPassword){
		//check to see if user exists.
		//check to see if the code is correct and if we are still within the allotted time. 
		//"This code is invalid or has expired. Try entering the code again or sending a new one."
		//update with new password. 
		//show confirm message.
		
		try{
			User user = UserDAO.getInstance().getUser(openIdProviderId, "none");
			if (user == null)
				return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.NO_SUCH_USER_MESSAGE));
			int userId = user.getId();
			
			//if this user has not requested a password change, send back an error. 
			PasswordResetRequest request = PasswordResetRequestDAO.getInstance().getRequest(userId);
			if (request == null)
				return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.INCORRECT_OR_EXPIRED_MESSAGE));
				
			//check to see if the code has not expired and if the code is correct.
			if ((new Date()).getTime() - request.getRequestTime().getTime() > RESET_PASSWORD_HOURS_BEFORE_EXPIRE * 60 * 60 * 1000
					|| !code.equals(request.getAuthenticationCode()))
				return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.INCORRECT_OR_EXPIRED_MESSAGE));
			
			String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
			
			UserDAO.getInstance().updateUser(
					user.getId(), 
					user.getFirstName(), 
					user.getLastName(), 
					user.getEmail(), 
					encryptedPassword, 
					user.getAffiliation(), 
					user.getBioportalUserId(), 
					user.getBioportalAPIKey());
			
			//remove the key so that it cannot be used again. 
			PasswordResetRequestDAO.getInstance().removeRequests(userId);
			
			return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(true, PasswordResetResult.PASSWORD_RESET_MESSAGE));
		} catch(Exception e){
			e.printStackTrace();
			return new RPCResult<PasswordResetResult>(true, new PasswordResetResult(false, PasswordResetResult.GENERIC_ERROR_MESSAGE));
		}
	}
	
	
	@Override
	public RPCResult<RequestCaptchaResult> requestCaptcha() {
		int id = CaptchaManager.getInstance().addCaptcha();

		return new RPCResult<RequestCaptchaResult>(true, new RequestCaptchaResult(id));
	}

	

	
	
	private String generatePasswordResetCode(){	
		Random rand = new Random(); //Random number generator. 
		int codeLengthModifier = rand.nextInt(4);
		String code = "";
		for (int i = 0; i < 5+codeLengthModifier; i++){
			char randomLetter = (char)(65+rand.nextInt(26) + ((rand.nextInt(2) == 0) ? 32 : 0));
			code += randomLetter;
		}
		
		return code;
	}

	@Override
	public RPCResult<String> getOperator(AuthenticationToken authenticationToken) {
		RPCResult<User> userResult = this.getUser(authenticationToken);
		if(!userResult.isSucceeded()) 
			return new RPCResult<String>(false, userResult.getMessage(), "");
		
		User user = userResult.getData();
		String operator = user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ") ";
		if(!user.getAffiliation().isEmpty()) 
			operator += "at " + user.getAffiliation();
		return new RPCResult<String>(true, "", operator);
	}
}
