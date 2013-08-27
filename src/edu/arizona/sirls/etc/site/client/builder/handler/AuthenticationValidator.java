package edu.arizona.sirls.etc.site.client.builder.handler;

import java.util.HashSet;
import java.util.Set;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.auth.IAuthenticationClientListener;
import edu.arizona.sirls.etc.site.client.api.auth.lib.rpc.AuthenticationClient;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.dialog.LoginDialogBox;
import edu.arizona.sirls.etc.site.client.builder.dialog.MessageDialogBox;
import edu.arizona.sirls.etc.site.client.builder.lib.MainMenuBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public class AuthenticationValidator implements IAuthenticationClientListener {

	private static AuthenticationValidator instance;
	
	public static AuthenticationValidator getInstance() {
		if(instance == null)
			instance = new AuthenticationValidator();
		return instance;
	}
	
	private Set<IAuthenticationValidatorListener> listeners = new HashSet<IAuthenticationValidatorListener>();

	public void validate() {
		AuthenticationToken authenticationToken = Authentication.getInstance().getAuthenticationToken();
		AuthenticationClient authenticationClient = AuthenticationClient.getInstance();
		authenticationClient.addListener(this);
		authenticationClient.isValidSession(authenticationToken);
	}

	@Override
	public void notifyAuthentication(AuthenticationResult authenticationResult) {
		AuthenticationClient authenticationClient = AuthenticationClient.getInstance();
		authenticationClient.removeListener(this);
		for(IAuthenticationValidatorListener listener : listeners) 
			listener.notify(authenticationResult);
	}

	public boolean addListener(IAuthenticationValidatorListener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(IAuthenticationValidatorListener listener) {
		return listeners.remove(listener);
	}
}
