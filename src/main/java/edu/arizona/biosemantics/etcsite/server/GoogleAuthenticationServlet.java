package edu.arizona.biosemantics.etcsite.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.CreateOTOAccountException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidOTOAccountException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.OTOException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;

public class GoogleAuthenticationServlet extends HttpServlet {

	private static final long serialVersionUID = 6688917446498637833L;
	private IAuthenticationService authenticationService;
	private IUserService userService;

	@Inject
	public GoogleAuthenticationServlet(AuthenticationService authenticationService, UserService userService) {
		this.authenticationService = authenticationService;
		this.userService = userService;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/*int userID = Integer.parseInt(request.getParameter("userID"));
		String sessionID = request.getParameter("sessionID");
		String target = request.getParameter("target");
		
		AuthenticationResult authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(userID, sessionID));
		if(authenticationResult.getResult()) { 	
			RequestDispatcher rd = request.getRequestDispatcher("index.html");
			rd.forward(request, response);
		}*/
		
		String place = Window.Location.getParameter("place");
		String action = Window.Location.getParameter("action");
		String code = Window.Location.getParameter("code");
		if(code != null) {
			if(place != null && place.equals(SettingsPlace.class.getSimpleName())) {
				switch(action) {
				case "save_oto":
					try {
						userService.saveOTOAccount(Authentication.getInstance().getToken(), code);
						response.sendRedirect(response.encodeRedirectURL(Configuration.googleRedirectURI + "?#" + SettingsPlace.class.getSimpleName() + ":"));
					} catch (UserNotFoundException | InvalidOTOAccountException | OTOException e) {
						log(LogLevel.ERROR, "Problem saving oto account", e);
						response.sendRedirect(response.encodeRedirectURL(Configuration.googleRedirectURI + "#" + SettingsPlace.class.getSimpleName() + ":"));
					}
					break;
				case "create_oto":
					try {
						userService.createOTOAccount(Authentication.getInstance().getToken(), code);
						response.sendRedirect(response.encodeRedirectURL(Configuration.googleRedirectURI + "#" + SettingsPlace.class.getSimpleName() + ":"));
					} catch (CreateOTOAccountException | OTOException e) {
						log(LogLevel.ERROR, "Problem creating oto account", e);
						response.sendRedirect(response.encodeRedirectURL(Configuration.googleRedirectURI + "#" + SettingsPlace.class.getSimpleName() + ":"));
					}
					
					break;
				}
			}
		} 

		response.sendRedirect(response.encodeRedirectURL(Configuration.googleRedirectURI));
	}
	
}