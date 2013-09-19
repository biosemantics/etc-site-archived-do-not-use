package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.event.LoginEvent;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

public class LoginButtonClickHandler implements ClickHandler {

	private PasswordTextBox passwordField;
	private TextBox userField;
	private Button loginButton;
	private TitleCloseDialogBox sourceDialogBox;
	private IAuthenticationServiceAsync authenticationService;
	private HandlerManager eventBus;
	private GwtEvent<?> target;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Login");

	public LoginButtonClickHandler(HandlerManager eventBus, 
			IAuthenticationServiceAsync authenticationService,
			TextBox userField,	PasswordTextBox passwordField, Button loginButton, 
			TitleCloseDialogBox sourceDialogBox) {
		this.eventBus = eventBus;
		this.authenticationService = authenticationService;
		this.userField = userField;
		this.passwordField = passwordField;
		this.loginButton = loginButton;
		this.sourceDialogBox = sourceDialogBox;
	}

	public void onClick(ClickEvent event) {
		// First, we validate the input.
		String userName = userField.getText();
		String password = passwordField.getText();
		if (!FieldVerifier.isValidName(userName)) {
			messagePresenter.setMessage("Please enter at least some characters");
			messagePresenter.go();
			return;
		}
		loginButton.setEnabled(false);
		authenticationService.login(userName, password, loginCallback);
	}
	
	private AsyncCallback<AuthenticationResult> loginCallback = new AsyncCallback<AuthenticationResult>() {
		public void onSuccess(AuthenticationResult authenticationResult) {
			if(authenticationResult.getResult()) {
				if(sourceDialogBox != null)
					sourceDialogBox.hide();
				eventBus.fireEvent(new LoginEvent(authenticationResult.getUsername(), 
						authenticationResult.getSessionID()));
				eventBus.fireEvent(target);
			} else {
				messagePresenter.setMessage("Wrong username or password. Login failed.");
				messagePresenter.go();
				
			}
			loginButton.setEnabled(true);
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			loginButton.setEnabled(true);
		}
	};
	
	public void setTarget(GwtEvent<?> target) {
		this.target = target;
	}
}
