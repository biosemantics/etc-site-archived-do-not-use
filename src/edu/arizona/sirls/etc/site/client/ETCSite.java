package edu.arizona.sirls.etc.site.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.builder.IHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.FooterBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedInHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedOutHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartMenuBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ETCSite implements EntryPoint {
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {		
		IAuthenticationServiceAsync authenticationService = GWT.create(IAuthenticationService.class);
		AuthenticationToken authenticationToken = Authentication.getInstance().getAuthenticationToken();
		authenticationService.isValidSession(authenticationToken, validSessionCallback);
	}

	
	private AsyncCallback<AuthenticationResult> validSessionCallback = new AsyncCallback<AuthenticationResult>() {
		public void onSuccess(AuthenticationResult authenticationResult) {
			Session session = Session.getInstance();
			
			IHeaderBuilder headerBuilder;
			if(authenticationResult.getResult()) {
				headerBuilder = new LoggedInHeaderBuilder();
			} else {
				headerBuilder = new LoggedOutHeaderBuilder();
			}
			PageBuilder pageBuilder = new PageBuilder(
					new FooterBuilder(), 
					new StartContentBuilder(), 
					headerBuilder, 
					new StartMenuBuilder());
			session.setPageBuilder(pageBuilder);
			pageBuilder.build();
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
	
}
