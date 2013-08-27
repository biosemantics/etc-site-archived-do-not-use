package edu.arizona.sirls.etc.site.client.builder.lib.settings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class SubmitSettingsClickHandler implements ClickHandler {

	private PasswordTextBox oldPasswordTextBox;
	private PasswordTextBox newPasswordTextBox;

	public SubmitSettingsClickHandler(PasswordTextBox oldPasswordTextBox, PasswordTextBox newPasswordTextBox) { 
		this.oldPasswordTextBox = oldPasswordTextBox;
		this.newPasswordTextBox = newPasswordTextBox;
	}
	
	@Override
	public void onClick(ClickEvent event) {

		//service do change; return if worked or not
		oldPasswordTextBox.getText();
		newPasswordTextBox.getText();
	}

}
