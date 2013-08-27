package edu.arizona.sirls.etc.site.server.rpc;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AuthenticationService extends RemoteServiceServlet implements IAuthenticationService {

	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public AuthenticationResult login(String user, String password) {
		String hash = BCrypt.hashpw(password, BCrypt.gensalt());
		//(create new user entry in db storing ONLY username and hash, *NOT* the password).
		if(user.equals("test"))
			return new AuthenticationResult(true, "sessionID", user);
		return new AuthenticationResult(false, null, null);
	}

	@Override
	public AuthenticationResult isValidSession(AuthenticationToken authentication) {
		String sessionID = authentication.getSessionID();
		if(sessionID != null)
			return new AuthenticationResult(true, sessionID, "test");
		return new AuthenticationResult(false, null, null);
	}
	
	
	
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	/*private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}*/

}
