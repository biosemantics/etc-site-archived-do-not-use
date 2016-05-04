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
import com.google.gwt.user.client.ui.CaptchaPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegisterView extends Composite implements IRegisterView {

	private static RegisterViewUiBinder uiBinder = GWT.create(RegisterViewUiBinder.class);

	interface RegisterViewUiBinder extends UiBinder<Widget, RegisterView> {
	}
	
	@UiField
	TextBox firstNameBox;
	
	@UiField
	TextBox lastNameBox;
	
	@UiField
	TextBox emailBox;
	
	@UiField
	PasswordTextBox passwordBox;
	
	@UiField
	PasswordTextBox confirmPasswordBox;
	
	@UiField
	Label errorLabel;
	
	@UiField
	CaptchaPanel captchaPanel;
	
	
	private Presenter presenter;
	private static final int FIELD_WIDTH = 180;
	
	public RegisterView() {
		initWidget(uiBinder.createAndBindUi(this));
		firstNameBox.setPixelSize(FIELD_WIDTH, 14);
		lastNameBox.setPixelSize(FIELD_WIDTH, 14);
		emailBox.setPixelSize(FIELD_WIDTH, 14);
		passwordBox.setPixelSize(FIELD_WIDTH, 14);
		confirmPasswordBox.setPixelSize(FIELD_WIDTH, 14);
		
		KeyPressHandler keyHandler = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					presenter.onCreate();
				else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
					presenter.onCancel();
			} 
		};
		
		firstNameBox.addKeyPressHandler(keyHandler);
		lastNameBox.addKeyPressHandler(keyHandler);
		emailBox.addKeyPressHandler(keyHandler);
		passwordBox.addKeyPressHandler(keyHandler);
		confirmPasswordBox.addKeyPressHandler(keyHandler);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("cancelButton")
	public void onCancelClick(ClickEvent event) {
		presenter.onCancel();
	}
	
	@UiHandler("createButton")
	public void onCreateClick(ClickEvent event) {
		presenter.onCreate();
	}

	@Override
	public String getFirstName() {
		return firstNameBox.getText();
	}

	@Override
	public String getLastName() {
		return lastNameBox.getText();
	}

	@Override
	public String getEmail() {
		return emailBox.getText();
	}

	@Override
	public String getPassword() {
		return passwordBox.getText();
	}
	
	@Override
	public String getConfirmPassword() {
		return confirmPasswordBox.getText();
	}
	
	
	@Override 
	public void setErrorMessage(String str){
		errorLabel.setText(str);
	}
	
	@Override
	public void clearPasswordBoxes(){
		passwordBox.setText("");
		confirmPasswordBox.setText("");
	}
	
	@Override
	public void clearAllBoxes(){
		firstNameBox.setText("");
		lastNameBox.setText("");
		emailBox.setText("");
		passwordBox.setText("");
		confirmPasswordBox.setText("");
		captchaPanel.clearText();
	}
	
	@Override
	public void giveFocus(){
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
	        public void execute () {
	        	if (firstNameBox.getText().length() == 0)
	        		firstNameBox.setFocus(true);
	        	else
	        		passwordBox.setFocus(true);
	        }
	    });
	}

	@Override
	public CaptchaPanel getCaptchaPanel() {
		return captchaPanel;
	}
}
