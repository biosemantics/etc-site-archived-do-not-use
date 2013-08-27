package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.dialog.MessageDialogBox;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedInHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.MainMenuBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartContentBuilder;
import edu.arizona.sirls.etc.site.client.api.auth.IAuthenticationClientListener;
import edu.arizona.sirls.etc.site.client.api.auth.lib.rpc.AuthenticationClient;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

public class LoginButtonClickHandler implements ClickHandler, IAuthenticationClientListener {

	private PasswordTextBox passwordField;
	private TextBox userField;
	private Button loginButton;
	private AuthenticationClient loginClient;
	private MessageDialogBox dialogBox;
	private DialogBox source;

	public LoginButtonClickHandler(TextBox userField,
			PasswordTextBox passwordField, Button loginButton, DialogBox source, PageBuilder targetPageBuidler) {
		this.userField = userField;
		this.passwordField = passwordField;
		this.loginButton = loginButton;
		this.loginClient = AuthenticationClient.getInstance();
		this.dialogBox = new MessageDialogBox("Login");
		this.source = source;
	}

	public void onClick(ClickEvent event) {
		// First, we validate the input.
		String userName = userField.getText();
		String password = passwordField.getText();
		if (!FieldVerifier.isValidName(userName)) {
			dialogBox.setResponse("Please enter at least some characters");
			return;
		}
		loginButton.setEnabled(false);
		loginClient.addListener(this);
		loginClient.login(userName, password);
	}

	@Override
	public void notifyAuthentication(AuthenticationResult authenticationResult) {
		loginClient.removeListener(this);
		if(authenticationResult.getResult()) {
			if(source != null)
				source.hide();
			Authentication authentication = Authentication.getInstance();
			authentication.setSessionID(authenticationResult.getSessionID());
			authentication.setUsername(authenticationResult.getUsername());
			Session session = Session.getInstance();
			PageBuilder pageBuilder = session.getPageBuilder();
			pageBuilder.setHeaderBuilder(LoggedInHeaderBuilder.getInstance());
			//pageBuilder.setMenuBuilder(MainMenuBuilder.getInstance());
			//pageBuilder.setContentBuilder(StartContentBuilder.getInstance());
			pageBuilder.build();
		} else {
			dialogBox.setResponse("Wrong username or password. Login failed.");
			dialogBox.center();
			dialogBox.setCloseButtonFocus(true);
			loginButton.setEnabled(true);
		}
		loginClient.removeListener(this);
	}
}
