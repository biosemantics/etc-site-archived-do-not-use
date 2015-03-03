package edu.arizona.biosemantics.etcsite.server.rpc.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.shared.model.Captcha;
import edu.arizona.biosemantics.etcsite.shared.model.PasswordResetRequest;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IncorrectCaptchaSolutionException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.InvalidPasswordResetException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.NoSuchUserException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.OpenPasswordResetRequestException;

/**
 * The server side implementation of the RPC service.
 */
public class AuthenticationService extends RemoteServiceServlet implements IAuthenticationService {
	
	private static int RESET_PASSWORD_MINIMUM_WAIT_TIME_SECONDS = 60;
	private static int RESET_PASSWORD_HOURS_BEFORE_EXPIRE = 1;
	
	private SecretKey key;
	private Cipher encryptCipher;
	private Cipher decryptCipher;
	private DAOManager daoManager = new DAOManager();
	private Emailer emailer = new Emailer();
	
	public AuthenticationService() {
		try {
			key = KeyGenerator.getInstance("DES").generateKey();
			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException e) {
			log(LogLevel.ERROR, "Couldn't find algorithm", e);
		} catch (NoSuchPaddingException e) {
			log(LogLevel.ERROR, "Invalid padding", e);
		} catch (InvalidKeyException e) {
			log(LogLevel.ERROR, "Invalid key", e);
		}
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public AuthenticationResult login(String email, String password) {
		User user = daoManager.getUserDAO().getUser(email);
		if(user != null && BCrypt.checkpw(password, user.getPassword())) {
			String sessionId = generateSessionId(user);
			return new AuthenticationResult(true, sessionId, new ShortUser(user));
		}
		return new AuthenticationResult(false, null, null);
	}
	
	@Override
	public AuthenticationResult loginWithGoogle(String accessToken) {
		URL url;
		HttpURLConnection connection = null;
		try {
			url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
		} catch (MalformedURLException e) {
			log(LogLevel.ERROR, "Malformed url", e);
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't read from url", e);
		}
		//int responseCode = connection.getResponseCode();
		//System.out.println("Sending GET request to " + url.getPath());
		//System.out.println("Response code: " + responseCode);
		
		if(connection != null) {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = reader.readLine()) != null){
					response.append(line);
				}
				//System.out.println("Got the result: \n" + response.toString());
				
				String firstName = null;
				String lastName = null;
				String openIdProviderId = null;
				try {
					JSONObject elements = new JSONObject(response.toString());
					firstName = elements.getString("given_name");
					lastName = elements.getString("family_name");
					openIdProviderId = elements.getString("email");
				} catch(JSONException e) {
					log(LogLevel.ERROR, "Couldn't parse JSON", e);
				}
				
				if(firstName != null && lastName != null && openIdProviderId != null) {
					//create an account for this user if they do not have one yet.	
					String dummyPassword = firstName + lastName;
					String encryptedDummyPassword = BCrypt.hashpw(dummyPassword, BCrypt.gensalt()); //encrypt password
					
					User user = daoManager.getUserDAO().getUser(openIdProviderId);
					if (user == null) { 
						user = daoManager.getUserDAO().insert(
								new User(openIdProviderId, "google", encryptedDummyPassword, firstName, lastName, "", "", ""));
					}

					String sessionId = generateSessionId(user);
					return new AuthenticationResult(true, sessionId, new ShortUser(user));
				}
			} catch (IOException e) {
				log(LogLevel.ERROR, "Couldn't open or close reader", e);
			}
		}
		return new AuthenticationResult(false, null, null); 
	}
	
	@Override
	public AuthenticationResult isValidSession(AuthenticationToken authenticationToken) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new AuthenticationResult(true, "admin", new ShortUser());
		
		User user = getUser(authenticationToken.getSessionID());
		if(user != null)
			return new AuthenticationResult(true, authenticationToken.getSessionID(), new ShortUser(user));
		return new AuthenticationResult(false, null, null);
	}
	
	private String generateSessionId(User user){
		String sessionId = user.getId() + "::" + user.getCreated().toString();
		return DatatypeConverter.printBase64Binary(sessionId.getBytes());
	}
	
	private User getUser(String sessionId) {
		if(sessionId != null) {
			String completeString = new String(DatatypeConverter.parseBase64Binary(sessionId));
			int colonIndex = completeString.indexOf("::");
			if(colonIndex == -1)
				return null;
			int userId = Integer.parseInt(completeString.substring(0, colonIndex));
			User user = daoManager.getUserDAO().getUser(userId);
			return user;
		}
		return null;
	}
	
	@Override
	public void requestPasswordResetCode(int captchaId, String captchaSolution, String email) 
			throws IncorrectCaptchaSolutionException, NoSuchUserException, OpenPasswordResetRequestException {
		if (!daoManager.getCaptchaDAO().isValidSolution(captchaId, captchaSolution))
			throw new IncorrectCaptchaSolutionException();
	
		User user = daoManager.getUserDAO().getUser(email);
		if (user == null)
			throw new NoSuchUserException();
		
		PasswordResetRequest oldRequest = daoManager.getPasswordResetRequestDAO().get(user);
		if (oldRequest != null) {
			if ((new Date()).getTime() - oldRequest.getRequestTime().getTime() < RESET_PASSWORD_MINIMUM_WAIT_TIME_SECONDS * 1000)
				throw new OpenPasswordResetRequestException();
			daoManager.getPasswordResetRequestDAO().remove(user);
		}
		String authenticationCode = generatePasswordResetCode();
		daoManager.getPasswordResetRequestDAO().insert(user, authenticationCode);
		String expireTime = RESET_PASSWORD_HOURS_BEFORE_EXPIRE + (RESET_PASSWORD_HOURS_BEFORE_EXPIRE == 1 ? " hour" : " hours");
		String bodyText = Configuration.passwordResetBody.replace("<openidproviderid>", email).replace("<code>", authenticationCode).replace("<expire>", expireTime);
		emailer.sendEmail(email, Configuration.passwordResetSubject, bodyText);
	}
	
	
	@Override
	public void resetPassword(String email, String code, String newPassword) throws NoSuchUserException, InvalidPasswordResetException {
		User user = daoManager.getUserDAO().getUser(email);
		if (user == null)
			throw new NoSuchUserException();
		
		PasswordResetRequest request = daoManager.getPasswordResetRequestDAO().get(user);
		if (request == null)
			throw new InvalidPasswordResetException();
			
		if((new Date()).getTime() - request.getRequestTime().getTime() > RESET_PASSWORD_HOURS_BEFORE_EXPIRE * 60 * 60 * 1000
				|| !code.equals(request.getAuthenticationCode()))
			throw new InvalidPasswordResetException();
			
		String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		user.setPassword(encryptedPassword);
		daoManager.getUserDAO().update(user);
		daoManager.getPasswordResetRequestDAO().remove(user);
	}
	
	@Override
	public Captcha createCaptcha() {
		Captcha captcha = daoManager.getCaptchaDAO().insert();
		return captcha;
	}	
	
	private String generatePasswordResetCode(){	
		Random random = new Random();
		int codeLengthModifier = random.nextInt(4);
		String code = "";
		for (int i = 0; i < 5+codeLengthModifier; i++){
			char randomLetter = (char)(65+random.nextInt(26) + ((random.nextInt(2) == 0) ? 32 : 0));
			code += randomLetter;
		}
		
		return code;
	}
}
