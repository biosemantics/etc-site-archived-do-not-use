package edu.arizona.biosemantics.etcsite.client.common;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.Cookies;

import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;

public class Authentication {

	private static Authentication instance;
	private String externalAccessToken;
	
	public static Authentication getInstance() {
		if(instance == null)
			instance = new Authentication(); 
		return instance;
	}
	
	public void setSessionID(String sessionID) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.sessionID, sessionID, expires, null, "/", false);
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

	public void setUserId(int arg) {
		final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.userId, String.valueOf(arg), expires, null, "/", false);
	}
	
	public void setEmail(String email) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.email, email, expires, null, "/", false);
	}
	
	public String getEmail() {
		String email = Cookies.getCookie(CookieVariable.email);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(email != null && email.equals("null"))
			email = null;
		return email;
	}
	
	public void setFirstName(String firstName) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.firstName, firstName, expires, null, "/", false);
	}
	
	public String getFirstName() {
		String firstName = Cookies.getCookie(CookieVariable.firstName);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(firstName != null && firstName.equals("null"))
			firstName = null;
		return firstName;
	}
	
	public void setLastName(String lastName) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.lastName, lastName, expires, null, "/", false);
	}

	public String getLastName() {
		String lastName = Cookies.getCookie(CookieVariable.lastName);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(lastName != null && lastName.equals("null"))
			lastName = null;
		return lastName;
	}
	
	public void setAffiliation(String affiliation) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.affiliation, affiliation, expires, null, "/", false);
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
