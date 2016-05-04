package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.Cookies;

import edu.arizona.biosemantics.etcsite.shared.model.Setup;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public class Authentication {

	private static Authentication instance;
	private String externalAccessToken;
	private Setup setup = ServerSetup.getInstance().getSetup();
	
	public static Authentication getInstance(){
		if(instance == null)
			instance = new Authentication();
		return instance;
	}
	
	public void setSessionID(String sessionID) {
		Cookies.setCookie(CookieVariable.sessionID, sessionID, null, null, "/", false);
	}
	
	public String getSessionId() {
		String sessionID = Cookies.getCookie(CookieVariable.sessionID);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(sessionID != null && sessionID.equals("null"))
			sessionID = null;
		return sessionID;
	}
	
	public int getUserId() {
		String userId = Cookies.getCookie(CookieVariable.userId); 
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(userId == null || (userId != null && userId.equals("null")))
			return -1;
		return Integer.parseInt(userId);
	}

	public void setUserId(int userId) {
		Cookies.setCookie(CookieVariable.userId, String.valueOf(userId), null, null, "/", false);
	}
	
	public void setEmail(String email) {
		Cookies.setCookie(CookieVariable.email, email, null, null, "/", false);
	}
	
	public String getEmail() {
		String email = Cookies.getCookie(CookieVariable.email);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(email != null && email.equals("null"))
			email = null;
		return email;
	}
	
	public void setFirstName(String firstName) {
		Cookies.setCookie(CookieVariable.firstName, firstName, null, null, "/", false);
	}
	
	public String getFirstName() {
		String firstName = Cookies.getCookie(CookieVariable.firstName);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(firstName != null && firstName.equals("null"))
			firstName = null;
		return firstName;
	}
	
	public void setLastName(String lastName) {
		Cookies.setCookie(CookieVariable.lastName, lastName, null, null, "/", false);
	}

	public String getLastName() {
		String lastName = Cookies.getCookie(CookieVariable.lastName);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(lastName != null && lastName.equals("null"))
			lastName = null;
		return lastName;
	}
	
	public void setAffiliation(String affiliation) {
		Cookies.setCookie(CookieVariable.affiliation, affiliation, null, null, "/", false);
	}
	
	public String getAffiliation() {
		String affiliation = Cookies.getCookie(CookieVariable.affiliation);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(affiliation != null && affiliation.equals("null"))
			affiliation = null;
		return affiliation;
	}
	
	public String getExternalAccessToken() {
		return externalAccessToken;
	}

	public void setExternalAccessToken(String externalAccessToken) {
		this.externalAccessToken = externalAccessToken;
	}
	
	public void destroy() {
		Cookies.removeCookie(CookieVariable.sessionID, "/");
		Cookies.removeCookie(CookieVariable.userId, "/");
		Cookies.removeCookie(CookieVariable.email, "/");
		Cookies.removeCookie(CookieVariable.firstName, "/");
		Cookies.removeCookie(CookieVariable.lastName, "/");
		Cookies.removeCookie(CookieVariable.affiliation, "/");
		
		externalAccessToken = null;
	}

	public AuthenticationToken getToken() {
		return new AuthenticationToken(this.getUserId(), this.getSessionId());
	}
	
	public boolean isSet() {
		return this.getUserId() != -1 && this.getSessionId() != null;
	}
}
