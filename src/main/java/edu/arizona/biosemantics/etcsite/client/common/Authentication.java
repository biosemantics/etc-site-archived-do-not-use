package edu.arizona.biosemantics.etcsite.client.common;

import java.util.Date;

import com.google.gwt.user.client.Cookies;

import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;

public class Authentication {

	private static Authentication instance;
	
	public static Authentication getInstance() {
		if(instance == null)
			instance = new Authentication(); 
		return instance;
	}
	
	private Authentication() { 	}
	
	public void setSessionID(String sessionID) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.sessionID, sessionID, expires, null, "/", false);
	}
	
	public String getSessionID() {
		String sessionID = Cookies.getCookie(CookieVariable.sessionID);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(sessionID != null && sessionID.equals("null"))
			sessionID = null;
		return sessionID;
	}
	
	public String getUsername() {
		String username = Cookies.getCookie(CookieVariable.username); 
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(username != null && username.equals("null"))
			username = null;
		return username;
	}

	public void setUsername(String username) {
		final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.username, username, expires, null, "/", false);
	}
	
	public void destroy() {
		Cookies.removeCookie(CookieVariable.sessionID, "/");
		Cookies.removeCookie(CookieVariable.username, "/");
	}

	public AuthenticationToken getToken() {
		return new AuthenticationToken(this.getUsername(), this.getSessionID());
	}
	
	public boolean isSet() {
		return this.getUsername() != null && this.getSessionID() != null;
	}
	
}
