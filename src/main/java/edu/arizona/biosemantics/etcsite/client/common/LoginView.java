package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginView extends Composite implements ILoginView {

	private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

	interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {}

	@UiField
	Label headerLabel;
	
	@UiField
	TextBox usernameTextBox;
	
	@UiField
	PasswordTextBox passwordTextBox;
	
	@UiField
	Label registerLabel;
	
	private Presenter presenter;

	public LoginView() {
		initWidget(uiBinder.createAndBindUi(this));
		usernameTextBox.setPixelSize(165, 14);
		passwordTextBox.setPixelSize(165, 14);
		
		KeyPressHandler keyHandler = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				headerLabel.setText("Enter sign-in credentials below.");
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					presenter.onLogin();
			} 
		};
		
		usernameTextBox.addKeyPressHandler(keyHandler);
		passwordTextBox.addKeyPressHandler(keyHandler);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("loginButton")
	public void onLogin(ClickEvent event) {
		presenter.onLogin();
	}
	
	@UiHandler("registerLabel")
	public void onRegisterClick(ClickEvent event){
		presenter.onRegisterRequest();
	}
	
	@UiHandler("resetPasswordLabel")
	public void onResetPasswordClick(ClickEvent event){
		presenter.onResetPasswordRequest();
	}
	
	@UiHandler("googleButton")
	public void onSignInWithGoogleClick(ClickEvent event){
		presenter.onSignInWithGoogle();
	}
	
	/**
	 * If there is no entry in the email field box, gives focus to the email box. 
	 * Otherwise, gives focus to the password box. 
	 */
	@Override
	public void giveLoginFocus(){
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
	        public void execute () {
	        	if (usernameTextBox.getText().length() == 0)
	        		usernameTextBox.setFocus(true);
	        	else
	        		passwordTextBox.setFocus(true);
	        }
	    });
	}
	
	public void clearPasswordTextBox(){
		passwordTextBox.setText("");	
	}
	
	@Override
	public String getUsername() {
		return usernameTextBox.getText();
	}

	@Override
	public String getPassword() {
		return passwordTextBox.getText();
	}
	
	public void setEmail(String str){
		usernameTextBox.setText(str);
	}
	
	public void setMessage(String str){
		headerLabel.setText(str);
	}
}
