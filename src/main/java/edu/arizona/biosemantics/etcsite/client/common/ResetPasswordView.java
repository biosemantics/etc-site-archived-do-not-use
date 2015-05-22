package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptchaPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ResetPasswordView extends Composite implements IResetPasswordView {

	private static ResetPasswordViewUiBinder uiBinder = GWT.create(ResetPasswordViewUiBinder.class);

	interface ResetPasswordViewUiBinder extends UiBinder<Widget, ResetPasswordView> {}

	@UiField
	TextBox emailBox;

	@UiField
	TextBox authenticationCodeBox;
	
	@UiField
	PasswordTextBox newPasswordBox;
	
	@UiField
	PasswordTextBox confirmNewPasswordBox;
	
	@UiField

	Label errorLabel1;
	
	@UiField
	Label errorLabel2;
	
	@UiField
	CaptchaPanel captchaPanel;
	
	private Presenter presenter;

	public ResetPasswordView() {
		initWidget(uiBinder.createAndBindUi(this));
		authenticationCodeBox.setPixelSize(80, 14);
		newPasswordBox.setPixelSize(165, 14);
		confirmNewPasswordBox.setPixelSize(165, 14); 
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("sendCodeButton")
	public void onSendCodeClick(ClickEvent event) {
		presenter.onRequestCode();
	}
	
	@UiHandler("submitButton")
	public void onSubmitClick(ClickEvent event){
		presenter.onRequestReset();
	}
	

	@Override
	public String getEmail() {
		return emailBox.getText();
	}
	
	@Override
	public String getCode() {
		return authenticationCodeBox.getText();
	}

	@Override
	public String getNewPassword() {
		return newPasswordBox.getText();
	}
	
	@Override
	public String getConfirmNewPassword() {
		return confirmNewPasswordBox.getText();
	}
	
	@Override
	public void setEmail(String emailField) {
		emailBox.setText(emailField);
	}

	@Override
	public void setErrorLabel1(String str){
		errorLabel1.setText(str);
		errorLabel2.setText("");
	}
	
	@Override
	public void setErrorLabel2(String str){
		errorLabel2.setText(str);
		errorLabel1.setText("");
	}
	
	@Override
	public void clearFields() {
		authenticationCodeBox.setText("");
		newPasswordBox.setText("");
		confirmNewPasswordBox.setText("");
		errorLabel1.setText("");
		errorLabel2.setText("");
	}
	
	@Override
	public CaptchaPanel getCaptchaPanel() {
		return captchaPanel;
	}
}

