package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.SettingsPresenter;

public class SettingsView extends Composite implements SettingsPresenter.Display {

	private Label oldPasswordLabel = new Label("Old Password");
	private PasswordTextBox oldPasswordTextBox = new PasswordTextBox();
	private Label newPasswordLabel = new Label("New Password");
	private PasswordTextBox newPasswordTextBox = new PasswordTextBox();
	private Button submitButton = new Button("Submit");
	
	public SettingsView() {
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='settingsContent'></div></div>");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(new Label("Change your password"));
		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, oldPasswordLabel);
		flexTable.setWidget(0,  1, oldPasswordTextBox);
		flexTable.setWidget(1, 0, newPasswordLabel);
		flexTable.setWidget(1, 1, newPasswordTextBox);
		flexTable.setWidget(2, 1, submitButton);
		verticalPanel.add(flexTable);
		htmlPanel.add(verticalPanel, "settingsContent");
		
		this.initWidget(htmlPanel);
	}

	@Override
	public Button getSubmitButton() {
		return this.submitButton;
	}

	@Override
	public HasText getOldPasswordTextBox() {
		return this.oldPasswordTextBox;
	}

	@Override
	public HasText getNewPasswordTextBox() {
		return this.newPasswordTextBox;
	}
	
}
