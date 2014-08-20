package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.User;

public class SettingsView extends Composite implements ISettingsView {

	private static SettingsViewUiBinder uiBinder = GWT.create(SettingsViewUiBinder.class);

	@UiTemplate("SettingsView.ui.xml")
	interface SettingsViewUiBinder extends UiBinder<Widget, SettingsView> {}

	@UiField
	Button submitButton;
	
	@UiField
	TextBox firstNameBox;
	
	@UiField
	TextBox lastNameBox;
	
	@UiField
	TextBox emailBox;
	
	@UiField
	TextBox affiliationBox;
	
	@UiField
	TextBox bioportalUserIdBox;
	
	@UiField
	TextBox bioportalAPIKeyBox;
	
	@UiField
	PasswordTextBox oldPasswordBox;
	
	@UiField
	PasswordTextBox newPasswordBox;
	
	@UiField
	PasswordTextBox confirmNewPasswordBox;
	
	@UiField
	Label idLabel;
	
	@UiField
	Label errorLabel;
	
	private Presenter presenter;
	private final int FIELD_WIDTH = 180;
	
	private User user;
	
	public SettingsView() {
		initWidget(uiBinder.createAndBindUi(this));
		firstNameBox.setPixelSize(FIELD_WIDTH, 14);
		lastNameBox.setPixelSize(FIELD_WIDTH, 14);
		emailBox.setPixelSize(FIELD_WIDTH, 14);
		affiliationBox.setPixelSize(FIELD_WIDTH, 14);
		bioportalUserIdBox.setPixelSize(FIELD_WIDTH, 14);
		bioportalAPIKeyBox.setPixelSize(FIELD_WIDTH, 14);
		oldPasswordBox.setPixelSize(FIELD_WIDTH, 14);
		newPasswordBox.setPixelSize(FIELD_WIDTH, 14);
		confirmNewPasswordBox.setPixelSize(FIELD_WIDTH, 14);
	}


	@UiHandler("submitButton")
	void onClick(ClickEvent e) {
		presenter.onSubmit();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void setData(User user){
		firstNameBox.setText(user.getFirstName());
		lastNameBox.setText(user.getLastName());
		idLabel.setText(user.getOpenIdProviderId());
		emailBox.setText(user.getEmail());
		affiliationBox.setText(user.getAffiliation());
		bioportalUserIdBox.setText(user.getBioportalUserId());
		bioportalAPIKeyBox.setText(user.getBioportalAPIKey());
		oldPasswordBox.setText("");
		newPasswordBox.setText("");
		confirmNewPasswordBox.setText("");
		
		firstNameBox.setEnabled(true);
		lastNameBox.setEnabled(true);
		emailBox.setEnabled(true);
		affiliationBox.setEnabled(true);
		bioportalUserIdBox.setEnabled(true);
		bioportalAPIKeyBox.setEnabled(true);
		oldPasswordBox.setEnabled(true);
		newPasswordBox.setEnabled(true);
		confirmNewPasswordBox.setEnabled(true);
		
		this.user = user;
		
		if (!user.getOpenIdProvider().equals("none")){
			firstNameBox.setEnabled(false);
			lastNameBox.setEnabled(false);
			oldPasswordBox.setText(user.getPassword());
			oldPasswordBox.setEnabled(false);
			newPasswordBox.setEnabled(false);
			confirmNewPasswordBox.setEnabled(false);
		}
	}
	
	@Override
	public void setErrorMessage(String str){
		errorLabel.setText(str);
	}
	
	@Override
	public void clearPasswords() {
		oldPasswordBox.setText("");
		newPasswordBox.setText("");
		confirmNewPasswordBox.setText("");
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
	public String getOpenIdProviderId(){
		return idLabel.getText(); 
	}
	
	@Override
	public String getEmail() {
		return emailBox.getText();
	}

	@Override
	public String getAffiliation() {
		return affiliationBox.getText();
	}

	@Override
	public String getBioportalUserId() {
		return bioportalUserIdBox.getText();
	}

	@Override
	public String getBioportalAPIKey() {
		return bioportalAPIKeyBox.getText();
	}

	@Override
	public String getOldPassword() {
		return oldPasswordBox.getText();
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
	public String getOpenIdProvider() {
		return user.getOpenIdProvider();
	}	
}
