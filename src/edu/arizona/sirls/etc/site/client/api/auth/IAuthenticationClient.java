package edu.arizona.sirls.etc.site.client.api.auth;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

public interface IAuthenticationClient {
	
	public void login(String userName, String password);
	
	public void isValidSession(AuthenticationToken authentication);

}
