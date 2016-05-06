package edu.arizona.biosemantics.etcsite.server.rpc.user;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.InvocationCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.client.common.CookieVariable;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.UserDAO;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.BCrypt;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.model.User.EmailPreference;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.CreateOTOAccountException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidOTOAccountException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidPasswordException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.OTOException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserAddException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.model.CreateUserResult;

public class UserService extends RemoteServiceServlet implements IUserService {
	
	private UserDAO userDAO;
	
	@Inject
	public UserService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public List<ShortUser> getUsers(AuthenticationToken authenticationToken, boolean includeSelf) {
		List<ShortUser> usernames;
		if(includeSelf)
			usernames = userDAO.getUsers();
		else
			usernames = userDAO.getUsersWithout(authenticationToken.getUserId());
		return usernames;
	}

	@Override
	public ShortUser getUser(AuthenticationToken authenticationToken) throws UserNotFoundException {
		int userId = authenticationToken.getUserId();
		ShortUser user = userDAO.getShortUser(userId);
		if(user != null){
			return user;
		} else { 
			throw new UserNotFoundException();
		}
	}
	
	@Override
	public ShortUser add(String firstName, String lastName, String email, String password) throws UserAddException {
		String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		if(!userDAO.hasUser(email)) {
			User user = userDAO.insert(new User(encryptedPassword, firstName, lastName, email, "", "", "", "", ""));
			if(user == null) {
				throw new UserAddException("Adding user failed!&nbsp; Please try again!");
			}
			return new ShortUser(user);
		} else 
			throw new UserAddException("Email already exists!");
	}
	
	@Override
	public ShortUser add(String openIdProviderId, String string, String firstName,
			String lastName, String password) throws UserAddException {
		String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		if(!userDAO.hasUser(openIdProviderId)) {
			User user = userDAO.insert(new User(openIdProviderId, "google", encryptedPassword, firstName, lastName, "", "", "", "", ""));
			if(user == null) {
				throw new UserAddException("Adding user failed");
			}
			return new ShortUser(user);
		} else 
			throw new UserAddException("Email already exists");
	}

	@Override
	public boolean existsUser(String email) {
		return userDAO.hasUser(email);
	}
	

	@Override
	public ShortUser updateName(AuthenticationToken authenticationToken, String firstName, String lastName, String affiliation) throws UserNotFoundException {
		User user = userDAO.getUser(authenticationToken.getUserId());
		if(user == null)
			throw new UserNotFoundException();			
		user.setAffiliation(affiliation);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		userDAO.update(user);
		
		return userDAO.getShortUser(authenticationToken.getUserId());
	}
	
	@Override
	public ShortUser updateBioportal(AuthenticationToken authenticationToken, String bioportalApiKey, String bioportalUserId) throws UserNotFoundException {
		User user = userDAO.getUser(authenticationToken.getUserId());
		if(user == null)
			throw new UserNotFoundException();			
		
		user.setBioportalAPIKey(bioportalApiKey);
		user.setBioportalUserId(bioportalUserId);
		userDAO.update(user);
		
		return userDAO.getShortUser(authenticationToken.getUserId());
	}
	
	@Override
	public ShortUser updateEmailNotification(AuthenticationToken authenticationToken,Map<String, Boolean>  profile) throws UserNotFoundException {
		User user = userDAO.getUser(authenticationToken.getUserId());
		if(user == null)
			throw new UserNotFoundException();			
		
		user.setProfile( profile);
		userDAO.update(user);
		
		return userDAO.getShortUser(authenticationToken.getUserId());
	}
	
	

	@Override
	public ShortUser update(AuthenticationToken authenticationToken, String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException { 
		User user = userDAO.getUser(authenticationToken.getUserId());
		if(user == null)
			throw new UserNotFoundException("User information not found! Please re-login again!");			
		
		if(!BCrypt.checkpw(oldPassword, user.getPassword())) {
			throw new InvalidPasswordException("Current password is not matched! Please try again!");
		}
		
		String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		user.setPassword(encryptedPassword);
		
		userDAO.update(user);
		return userDAO.getShortUser(authenticationToken.getUserId());
	}


	@Override
	public edu.arizona.biosemantics.oto.model.User saveOTOAccount(AuthenticationToken token, String googleCode) throws UserNotFoundException, InvalidOTOAccountException, OTOException {
		ShortUser shortUser = this.getUser(token);
		boolean share = true;
		GoogleUser googleUser;
		try {
			googleUser = this.getGoogleUserFromAccessToken(googleCode);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get google user", e);
			throw new UserNotFoundException(e);
		}
		String dummyPassword = Configuration.otoSecret + ":" + googleUser.getId();
		saveOTOAccount(token, share, googleUser.getEmail(), dummyPassword);
		edu.arizona.biosemantics.oto.model.User otoUser = new edu.arizona.biosemantics.oto.model.User();
		otoUser.setUserEmail(googleUser.getEmail());
		otoUser.setFirstName(googleUser.getFirstName());
		otoUser.setLastName(googleUser.getLastName());
		otoUser.setPassword(dummyPassword);
		otoUser.setAffiliation(shortUser.getAffiliation());
		otoUser.setFirstName(shortUser.getFirstName());
		otoUser.setLastName(shortUser.getLastName());
		otoUser.setBioportalUserId(shortUser.getBioportalUserId());
		otoUser.setBioportalApiKey(shortUser.getBioportalApiKey());
		return otoUser;
	}	

	@Override
	public void saveOTOAccount(AuthenticationToken authenticationToken, boolean share, String email, String password) throws InvalidOTOAccountException, OTOException {
		log(LogLevel.DEBUG, "here: " + email + " " + password);
		User user = userDAO.getUser(authenticationToken.getUserId());
		
		if(share) {
			try (OTOClient otoClient = new OTOClient(Configuration.otoUrl)) {
				otoClient.open();
				edu.arizona.biosemantics.oto.model.User otoUser = new edu.arizona.biosemantics.oto.model.User();
				otoUser.setUserEmail(email);
				otoUser.setPassword(password);
				Future<String> tokenResult = otoClient.getUserAuthenticationToken(otoUser); 
				String token = tokenResult.get();
				if(token != null && !token.isEmpty()) {
					user.setOtoAccountEmail(otoUser.getUserEmail());
					user.setOtoAuthenticationToken(token);
					userDAO.update(user);
				} else {
					user.setOtoAccountEmail("");
					user.setOtoAuthenticationToken("");
					userDAO.update(user);
					throw new InvalidOTOAccountException();
				}
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Problem saving OTO Account", e);
				throw new OTOException(e);
			}
		} else {
			user.setOtoAccountEmail("");
			user.setOtoAuthenticationToken("");
			userDAO.update(user);
		}
	}
	
	@Override
	public edu.arizona.biosemantics.oto.model.User createOTOAccount(AuthenticationToken token, String email, String password) throws CreateOTOAccountException {
		ShortUser shortUser;
		try {
			shortUser = this.getUser(token);
		} catch (UserNotFoundException e) {
			throw new CreateOTOAccountException(e);
		}
		
		edu.arizona.biosemantics.oto.model.User otoUser = new edu.arizona.biosemantics.oto.model.User();
		otoUser.setUserEmail(email);
		otoUser.setPassword(password);
		otoUser.setAffiliation(shortUser.getAffiliation());
		otoUser.setFirstName(shortUser.getFirstName());
		otoUser.setLastName(shortUser.getLastName());
		otoUser.setBioportalUserId(shortUser.getBioportalUserId());
		otoUser.setBioportalApiKey(shortUser.getBioportalApiKey());
		try (OTOClient otoClient = new OTOClient(Configuration.otoUrl)) {
			otoClient.open();
			Future<CreateUserResult> createResult = otoClient.postUser(otoUser);
			CreateUserResult result = createResult.get();
			if(!result.isResult())
				throw new CreateOTOAccountException(result.getMessage());
			
			return otoUser;
		}  catch (InterruptedException | ExecutionException e) { 
			log(LogLevel.ERROR, "Problem creating OTO Account", e);
			throw new CreateOTOAccountException(e);
		}
	}
	
	//TODO: use https://code.google.com/p/google-api-java-client/w/list
	@Override
	public edu.arizona.biosemantics.oto.model.User createOTOAccount(AuthenticationToken token, String googleCode) throws CreateOTOAccountException, OTOException {
		GoogleUser googleUser;
		try {
			googleUser = getGoogleUserFromAccessToken(googleCode);
			log(LogLevel.DEBUG, "Google user: " + googleUser.toString());
		} catch (Exception e) {
			throw new CreateOTOAccountException(e);
		}
		
		ShortUser shortUser;
		try {
			shortUser = this.getUser(token);
		} catch (UserNotFoundException e) {
			throw new CreateOTOAccountException(e);
		}
		
		edu.arizona.biosemantics.oto.model.User otoUser = new edu.arizona.biosemantics.oto.model.User();
		String dummyPassword = Configuration.otoSecret + ":" + googleUser.getId();
		otoUser.setUserEmail(googleUser.getEmail());
		otoUser.setFirstName(googleUser.getFirstName());
		otoUser.setLastName(googleUser.getLastName());
		otoUser.setPassword(dummyPassword);
		otoUser.setAffiliation(shortUser.getAffiliation());
		otoUser.setFirstName(shortUser.getFirstName());
		otoUser.setLastName(shortUser.getLastName());
		otoUser.setBioportalUserId(shortUser.getBioportalUserId());
		otoUser.setBioportalApiKey(shortUser.getBioportalApiKey());
		
		try (OTOClient otoClient = new OTOClient(Configuration.otoUrl)) {
			otoClient.open();
			Future<CreateUserResult> createResult = otoClient.postUser(otoUser);
			CreateUserResult result = createResult.get();
			if(!result.isResult())
				throw new CreateOTOAccountException(result.getMessage());
			try {
				saveOTOAccount(token, true, googleUser.getEmail(), dummyPassword);
			} catch (InvalidOTOAccountException e) {
				log(LogLevel.ERROR, "Problem saving OTO Account", e);
				throw new CreateOTOAccountException(e);
			}
			return otoUser;
		}  catch (InterruptedException | ExecutionException e) {
			log(LogLevel.ERROR, "Problem creating OTO Account", e);
			throw new CreateOTOAccountException(e);
		}
	}
	
	private GoogleUser getGoogleUserFromAccessToken(String accessToken) throws Exception {		
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
		HttpResponse response = httpclient.execute(httpGet);
		
		String tokenType = null;
		String expiresIn = null;
		String idToken = null;
		try {
			JSONObject elements = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
			accessToken = elements.getString("access_token");
			tokenType = elements.getString("token_type");
			expiresIn = elements.getString("expires_in");
			idToken = elements.getString("id_token"); 
		} catch(JSONException e) {
			log(LogLevel.ERROR, "Couldn't parse JSON", e);
		}
		if(accessToken != null) {
			HttpGet httpget = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken);
			response = httpclient.execute(httpget);
			String id = null;
			String firstName = null;
			String lastName = null;
			String email = null;
			try {
				JSONObject elements = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
				id = elements.getString("id");
				firstName = elements.getString("given_name");
				lastName = elements.getString("family_name");
				email = elements.getString("email"); 
				if(email != null && firstName != null && lastName != null)
					return new GoogleUser(id, firstName, lastName, email);
			} catch(JSONException e) {
				log(LogLevel.ERROR, "Couldn't parse JSON", e);
			}
		}
		return null;
	}
	
	private GoogleUser getGoogleUser(String googleCode) throws Exception {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://accounts.google.com/o/oauth2/token");

		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("code", googleCode));
		params.add(new BasicNameValuePair("client_id", Configuration.googleClientId));
		params.add(new BasicNameValuePair("client_secret", Configuration.googleSecret));
		params.add(new BasicNameValuePair("redirect_uri", Configuration.googleRedirectURI));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		UrlEncodedFormEntity w = new UrlEncodedFormEntity(params);
		httppost.setEntity(new UrlEncodedFormEntity(params));
		
		HttpResponse response = httpclient.execute(httppost);
		
		String accessToken = null;
		String tokenType = null;
		String expiresIn = null;
		String idToken = null;
		try {
			JSONObject elements = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
			accessToken = elements.getString("access_token");
			tokenType = elements.getString("token_type");
			expiresIn = elements.getString("expires_in");
			idToken = elements.getString("id_token"); 
		} catch(JSONException e) {
			log(LogLevel.ERROR, "Couldn't parse JSON", e);
		}
		if(accessToken != null) {
			HttpGet httpget = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken);
			response = httpclient.execute(httpget);
			String id = null;
			String firstName = null;
			String lastName = null;
			String email = null;
			try {
				JSONObject elements = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
				id = elements.getString("id");
				firstName = elements.getString("given_name");
				lastName = elements.getString("family_name");
				email = elements.getString("email"); 
				if(email != null && firstName != null && lastName != null)
					return new GoogleUser(id, firstName, lastName, email);
			} catch(JSONException e) {
				log(LogLevel.ERROR, "Couldn't parse JSON", e);
			}
		}
		return null;
	}

	@Override
	public boolean isProfile(AuthenticationToken token, Help help) {
		return userDAO.getUser(token.getUserId()).getProfileValue(Help.class + "_" + help.toString());
	}

	@Override
	public void setProfile(AuthenticationToken token, Help help, boolean dontShowPopup) {
		User user = userDAO.getUser(token.getUserId());
		user.setProfileValue(Help.class + "_" + help.toString(), dontShowPopup);
		userDAO.update(user);
	}

	@Override
	public boolean isProfile(AuthenticationToken token,	EmailPreference emailPreference) {
		return userDAO.getUser(token.getUserId()).getProfileValue(EmailPreference.class + "_" + emailPreference.toString());
	}

	@Override
	public void setProfile(AuthenticationToken token, EmailPreference emailPreference, boolean dontShowPopup) {
		User user = userDAO.getUser(token.getUserId());
		user.setProfileValue(EmailPreference.class + "_" + emailPreference.toString(), dontShowPopup);
		userDAO.update(user);
	}

	@Override
	public boolean hasLinkedOTOAccount(AuthenticationToken token) {
		User user = userDAO.getUser(token.getUserId());
		return user.hasOTOAuthenticationTokenAndEmail();
	}

}
