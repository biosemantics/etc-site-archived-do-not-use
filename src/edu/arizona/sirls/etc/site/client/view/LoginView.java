package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import edu.arizona.sirls.etc.site.client.presenter.LoginPresenter;

public class LoginView extends Composite implements LoginPresenter.Display {
	
	private Label messageLabel = new Label();
	private Label passwordLabel = new Label("Password:");
	private Label userNameLabel = new Label("User name:");
	private Button loginButton = new Button("Login");
	private Button cancelButton = new Button("Cancel");
	private TextBox userNameField = new TextBox();
	private PasswordTextBox passwordField = new PasswordTextBox();
	
	public LoginView() { 
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, messageLabel);
		table.getFlexCellFormatter().setColSpan(0, 0, 2);
		table.setWidget(1, 0, userNameLabel);
		table.setWidget(1, 1, userNameField);
		table.setWidget(2, 0, passwordLabel);
		table.setWidget(2, 1, passwordField);
		table.setWidget(3, 0, cancelButton);
		table.setWidget(3, 1, loginButton);
		
		this.initWidget(table);
	}

	@Override
	public Label getMessageLabel() {
		return this.messageLabel;
	}

	@Override
	public Button getCancelButton() {
		return this.cancelButton;
	}

	@Override
	public Button getLoginButton() {
		return this.loginButton;
	}

	@Override
	public TextBox getUserNameField() {
		return this.userNameField;
	}

	@Override
	public PasswordTextBox getPasswordField() {
		return this.passwordField;
	}

}
