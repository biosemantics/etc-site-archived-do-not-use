package edu.arizona.biosemantics.etcsite.client.common;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.Cookies;

import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;

public class Authentication {

	private static Authentication instance;
	private List<ChangeObserver> changeObservers;
	
	public static Authentication getInstance() {
		if(instance == null)
			instance = new Authentication(); 
		return instance;
	}
	
	private Authentication() {
		changeObservers = new LinkedList<ChangeObserver>();
	}
	
	
	public void setSessionID(String sessionID) {
	    final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.sessionID, sessionID, expires, null, "/", false);
	    
	    //notify observers.
	    for (ChangeObserver observer: changeObservers){
	    	observer.loginChanged();
	    }
	}
	
	public String getSessionID() {
		String sessionID = Cookies.getCookie(CookieVariable.sessionID);
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(sessionID != null && sessionID.equals("null"))
			sessionID = null;
		return sessionID;
	}
	
	public String getUserId() {
		String userId = Cookies.getCookie(CookieVariable.userId); 
		//the Cookie class does not actually return null but "null" String for cookies that do not exist; but in implementation?
		if(userId != null && userId.equals("null"))
			userId = null;
		return userId;
	}

	public void setUserId(String arg) {
		final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
	    Cookies.setCookie(CookieVariable.userId, arg, expires, null, "/", false);
	    
	    //notify observers.
	    for (ChangeObserver observer: changeObservers){
	    	observer.loginChanged();
	    }
	}
	
	
	public void destroy() {
		Cookies.removeCookie(CookieVariable.sessionID, "/");
		Cookies.removeCookie(CookieVariable.userId, "/");
	}

	public AuthenticationToken getToken() {
		return new AuthenticationToken(this.getUserId(), this.getSessionID());
	}
	
	public boolean isSet() {
		return this.getUserId() != null && this.getSessionID() != null;
	}
	
	public String getUsername(){ // to maintain compatibility, even though unique Id is used instead of username. 
		return getUserId();
	}
	
	public void addChangeObserver(ChangeObserver observer){
		changeObservers.add(observer);
	}
	
	public interface ChangeObserver{
		public void loginChanged();
	}
}
