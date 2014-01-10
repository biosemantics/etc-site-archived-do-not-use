package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

/**
 * The server side implementation of the RPC service.
 */
public class AuthenticationService extends RemoteServiceServlet implements IAuthenticationService {

	private static final long serialVersionUID = 5337745818086392785L;

	@Override
	protected void doUnexpectedFailure(Throwable t) {
	    t.printStackTrace(System.err);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public RPCResult<AuthenticationResult> login(String user, String password) {
		//String hash = BCrypt.hashpw(password, BCrypt.gensalt());
		//(create new user entry in db storing ONLY username and hash, *NOT* the password).
		if(user.equals("hong") || user.equals("elvis") || user.equals("thomas"))
			return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, "sessionID", user));
		return new RPCResult<AuthenticationResult>(true, "Authentication failed", new AuthenticationResult(false, null, null));
	}

	@Override
	public RPCResult<AuthenticationResult> isValidSession(AuthenticationToken authenticationToken) {
		if(authenticationToken instanceof AdminAuthenticationToken)
			return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, "admin", "admin"));
		String sessionID = authenticationToken.getSessionID();
		if(sessionID != null)
			return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(true, sessionID, authenticationToken.getUsername()));
		return new RPCResult<AuthenticationResult>(true, new AuthenticationResult(false, null, null));
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
