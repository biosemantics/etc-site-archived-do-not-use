package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import edu.arizona.sirls.etc.site.client.presenter.LoggedOutHeaderPresenter;

public class LoggedOutHeaderView extends Composite implements LoggedOutHeaderPresenter.Display {

	private ImageLabelComposite helpImageLabelComposite = 
			new ImageLabelComposite("images/Help.gif", "20px", "20px", "Video Tutorial");
	private PasswordTextBox passwordField = new PasswordTextBox();
	private TextBox userField = new TextBox();
	private Button loginButton = new Button("Login");

	public LoggedOutHeaderView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div class='helpIcon' id='helpIconLoggedOut'></div>" +
				"<div id='helpText'></div>" +
				"<div id='loginButton'></div>" +
				"<div id='loginButtonText'></div>" +
				"<div id='userField'></div>" +
				"<div id='passwordField'></div>" +
				"<div id='loginText'>User Login:</div>");
		
		htmlPanel.add(userField, "userField");
		htmlPanel.add(passwordField, "passwordField");
		htmlPanel.add(loginButton, "loginButton");
		htmlPanel.add(helpImageLabelComposite, "helpIconLoggedOut");
		
		/** Style related; Override standard css of GWT and always apply these styles to textfield/button? */
		userField.setHeight("25px");
		passwordField.setHeight("25px");
		loginButton.setHeight("25px");
		loginButton.getElement().getStyle().setPaddingTop(2, Unit.PX);
		
		this.initWidget(htmlPanel);
	}
	
	@Override
	public Button getLoginButton() {
		return this.loginButton;
	}

	@Override
	public TextBox getUserField() {
		return this.userField;
	}

	@Override
	public PasswordTextBox getPasswordField() {
		return this.passwordField;
	}

	@Override
	public ImageLabelComposite getHelpImageLabelComposite() {
		return this.helpImageLabelComposite;
	}
}
