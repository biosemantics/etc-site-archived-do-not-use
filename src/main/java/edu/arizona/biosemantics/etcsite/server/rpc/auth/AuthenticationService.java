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
import java.util.concurrent.Future;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.ws.rs.client.InvocationCallback;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.CaptchaDAO;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.PasswordResetRequestDAO;
import edu.arizona.biosemantics.etcsite.server.db.UserDAO;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.shared.model.Captcha;
import edu.arizona.biosemantics.etcsite.shared.model.PasswordResetRequest;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.CaptchaException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IncorrectCaptchaSolutionException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.InvalidPasswordResetException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.LoginGoogleResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.NoSuchUserException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.OpenPasswordResetRequestException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.RegistrationFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserAddException;

/**
 * The server side implementation of the RPC service.
 */
public class AuthenticationService extends RemoteServiceServlet implements IAuthenticationService {
	
	private static int RESET_PASSWORD_MINIMUM_WAIT_TIME_SECONDS = 60;
	private static int RESET_PASSWORD_HOURS_BEFORE_EXPIRE = 1;
	
	private SecretKey key;
	private Cipher encryptCipher;
	private Cipher decryptCipher;
	private Emailer emailer;
	private IUserService userService;
	private UserDAO userDAO;
	private CaptchaDAO captchaDAO;
	private PasswordResetRequestDAO passwordResetRequestDAO;
	
	@Inject
	public AuthenticationService(IUserService userService, UserDAO userDAO, CaptchaDAO captchaDAO, 
			PasswordResetRequestDAO passwordResetRequestDAO, Emailer emailer) {
		this.userService = userService;
		this.emailer = emailer;
		this.userDAO = userDAO;
		this.captchaDAO = captchaDAO;
		this.passwordResetRequestDAO = passwordResetRequestDAO;
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
		User user = userDAO.getUser(email);
		if(user != null && BCrypt.checkpw(password, user.getPassword())) {
			String sessionId = generateSessionId(user);
			return new AuthenticationResult(true, sessionId, new ShortUser(user));
		}
		return new AuthenticationResult(false, null, null);
	}
	
	@Override
	public LoginGoogleResult loginOrSignupWithGoogle(String accessToken) throws RegistrationFailedException {
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
				
				String id = null;
				String firstName = null;
				String lastName = null;
				String openIdProviderId = null;
				try {
					JSONObject elements = new JSONObject(response.toString());
					id = elements.getString("id");
					firstName = elements.getString("given_name");
					lastName = elements.getString("family_name");
					openIdProviderId = elements.getString("email");
				} catch(JSONException e) {
					log(LogLevel.ERROR, "Couldn't parse JSON", e);
				}
							
				if(id != null && firstName != null && lastName != null && openIdProviderId != null) {
					//create an account for this user if they do not have one yet.	
					String dummyPassword = Configuration.secret + ":" + id;
					
					User user = userDAO.getUser(openIdProviderId);
					boolean registerNew = user == null;
					if (registerNew) {
						user = addUser(firstName, lastName, openIdProviderId, dummyPassword, "google");
					}
					String sessionId = generateSessionId(user);
					return new LoginGoogleResult(new AuthenticationResult(true, sessionId, new ShortUser(user)), registerNew);
				}
			} catch (IOException e) {
				log(LogLevel.ERROR, "Couldn't open or close reader", e);
			}
		}
		return new LoginGoogleResult(new AuthenticationResult(false, null, null), false); 
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
			User user = userDAO.getUser(userId);
			return user;
		}
		return null;
	}
	
	@Override
	public void requestPasswordResetCode(int captchaId, String captchaSolution, String email) 
			throws IncorrectCaptchaSolutionException, NoSuchUserException, OpenPasswordResetRequestException {
		if (!captchaDAO.isValidSolution(captchaId, captchaSolution))
			throw new IncorrectCaptchaSolutionException("Scurity key is not correct! Please try agian!");
	
		User user = userDAO.getUser(email);
		if (user == null)
			throw new NoSuchUserException("No such user in ETC system! Please sign up first!");
		
		PasswordResetRequest oldRequest = passwordResetRequestDAO.get(user);
		if (oldRequest != null) {
			if ((new Date()).getTime() - oldRequest.getRequestTime().getTime() < RESET_PASSWORD_MINIMUM_WAIT_TIME_SECONDS * 1000)
				throw new OpenPasswordResetRequestException("You have already requested authentication code in one hour, please check you Email!");
			passwordResetRequestDAO.remove(user);
		}
		String authenticationCode = generatePasswordResetCode();
		passwordResetRequestDAO.insert(user, authenticationCode);
		String expireTime = RESET_PASSWORD_HOURS_BEFORE_EXPIRE + (RESET_PASSWORD_HOURS_BEFORE_EXPIRE == 1 ? " hour" : " hours");
		String bodyText = Configuration.passwordResetBody.replace("<openidproviderid>", email).replace("<code>", authenticationCode).replace("<expire>", expireTime);
		emailer.sendEmail(email, Configuration.passwordResetSubject, bodyText);
	}
	
	
	@Override
	public void resetPassword(String email, String code, String newPassword) throws NoSuchUserException, InvalidPasswordResetException {
		User user = userDAO.getUser(email);
		
		if (user == null)
			throw new NoSuchUserException("No such user in ETC system! Please sign up first!");
		
		PasswordResetRequest request = passwordResetRequestDAO.get(user);
		if (request == null)
			throw new InvalidPasswordResetException("Please send Authentication Code to your email address first!");
			
		if((new Date()).getTime() - request.getRequestTime().getTime() > RESET_PASSWORD_HOURS_BEFORE_EXPIRE * 60 * 60 * 1000)
			throw new InvalidPasswordResetException("The valid time of the code expired! Please try again!");
		
		if( (new Date()).getTime() - request.getRequestTime().getTime() <= RESET_PASSWORD_HOURS_BEFORE_EXPIRE * 60 * 60 * 1000&&!code.equals(request.getAuthenticationCode()))
			throw new InvalidPasswordResetException("Authentication code is not correct! Please try again!");
			
		String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		user.setPassword(encryptedPassword);
		userDAO.update(user);
		passwordResetRequestDAO.remove(user);
	}
	
	@Override
	public Captcha createCaptcha() {
		Captcha captcha = captchaDAO.insert();
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

	private User addUser(String firstName, String lastName, String email, String password, String openId) throws RegistrationFailedException {
		if(openId == null) {
			try {
				userService.add(firstName, lastName, email, password);
			} catch (UserAddException e) {
				throw new RegistrationFailedException(e.getMessage());
			}
		} else {
			try {
				userService.add(email, "google", firstName, lastName, password);
			} catch (UserAddException e) {
				throw new RegistrationFailedException(e.getMessage());
			}	
		}
		User user = userDAO.getUser(email);
		return user;
	}

	@Override
	public AuthenticationResult signupUser(int captchaId, String captchaSolution,
			String firstName, String lastName, String email, String password) throws CaptchaException, RegistrationFailedException {
		if (!captchaDAO.isValidSolution(captchaId, captchaSolution)){
			throw new CaptchaException("Security Code incorrect! Please try again!");
		}
		addUser(firstName, lastName, email, password, null);
		return this.login(email, password);
	}
}
