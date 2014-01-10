package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SettingsView extends Composite implements ISettingsView {

	private static SettingsViewUiBinder uiBinder = GWT.create(SettingsViewUiBinder.class);

	@UiTemplate("SettingsView.ui.xml")
	interface SettingsViewUiBinder extends UiBinder<Widget, SettingsView> {
	}

	@UiField
	Button submit;
	@UiField
	TextBox bioportalUserId;
	@UiField
	TextBox bioportalApiKey;
	@UiField
	PasswordTextBox oldPassword;
	@UiField
	PasswordTextBox newPassword;
	@UiField
	PasswordTextBox confirmNewPassword;
	private Presenter presenter;
	
	public SettingsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}


	@UiHandler("submit")
	void onClick(ClickEvent e) {
		presenter.onSubmit();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}


	@Override
	public String getConfirmedNewPassword() {
		return this.confirmNewPassword.getText();
	}


	@Override
	public String getOldPassword() {
		return this.oldPassword.getText();
	}


	@Override
	public String getNewPassword() {
		return this.newPassword.getText();
	}


	@Override
	public String getBioportalUserId() {
		return this.bioportalUserId.getText();
	}


	@Override
	public String getBioportalAPIKey() {
		return this.bioportalApiKey.getText();
	}

}
