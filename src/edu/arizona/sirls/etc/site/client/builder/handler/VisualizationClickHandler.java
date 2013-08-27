package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.dialog.LoginDialogBox;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedInHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.MainMenuBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.VisualizationContentBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

public class VisualizationClickHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		IAuthenticationServiceAsync authenticationService = GWT.create(IAuthenticationService.class);
		AuthenticationToken authenticationToken = Authentication.getInstance().getAuthenticationToken();
		authenticationService.isValidSession(authenticationToken, validSessionCallback);
	}

	private AsyncCallback<AuthenticationResult> validSessionCallback = new AsyncCallback<AuthenticationResult>() {
		public void onSuccess(AuthenticationResult authenticationResult) {
			Session session = Session.getInstance();
			PageBuilder pageBuilder = session.getPageBuilder();
			pageBuilder.setHeaderBuilder(new LoggedInHeaderBuilder());
			pageBuilder.setMenuBuilder(new MainMenuBuilder());
			pageBuilder.setContentBuilder(new VisualizationContentBuilder());
			if(authenticationResult.getResult()) {
				pageBuilder.build();
			} else {
				LoginDialogBox loginBox = new LoginDialogBox("You have to login to use this functionality", pageBuilder);
				loginBox.center();
			}
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
}
