package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.Date;

public class PasswordResetRequest implements Serializable{

	private static final long serialVersionUID = -1058828266337932771L;
	
	private int id;
	private String authenticationCode; 
	private Date requestTime;
	
	public PasswordResetRequest(int id, String authenticationCode, Date requestTime){
		this.id = id;
		this.authenticationCode = authenticationCode;
		this.requestTime = requestTime;		
	}

	public int getId() {
		return id;
	}

	public String getAuthenticationCode() {
		return authenticationCode;
	}

	public Date getRequestTime() {
		return requestTime;
	}
}
