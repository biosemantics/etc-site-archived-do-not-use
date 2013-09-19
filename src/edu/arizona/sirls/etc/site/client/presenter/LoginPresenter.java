package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

public class LoginPresenter {
	
	public interface Display {
		Label getMessageLabel();
		Button getCancelButton();
		Button getLoginButton();
		Widget asWidget();
		TextBox getUserNameField();
		PasswordTextBox getPasswordField(); 
	}

	private HandlerManager eventBus;
	private Display display;
	private IAuthenticationServiceAsync authenticationService;
	private TitleCloseDialogBox dialogBox;
	private LoginButtonClickHandler loginButtonClickHandler;

	public LoginPresenter(HandlerManager eventBus, Display display,
			IAuthenticationServiceAsync authenticationService, String message) {
		this.eventBus = eventBus;
		this.display = display;
		this.authenticationService = authenticationService;
		this.dialogBox = new TitleCloseDialogBox("Login");
		bind(message);
		
	}

	private void bind(String message) {
		display.getMessageLabel().setText(message);
		// Add a handler to close the DialogBox
		display.getCancelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		}); 
		
		this.loginButtonClickHandler = new LoginButtonClickHandler(eventBus, authenticationService, 
				display.getUserNameField(), display.getPasswordField(), 
				display.getLoginButton(), dialogBox);
		display.getLoginButton().addClickHandler(loginButtonClickHandler);
	}

	public void go() {
		dialogBox.clear();
		dialogBox.add(display.asWidget());
		dialogBox.center();
	}

	public void setTarget(GwtEvent<?> target) {
		loginButtonClickHandler.setTarget(target);
	}

}
