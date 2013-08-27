package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.IHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.handler.FieldVerifier;
import edu.arizona.sirls.etc.site.client.builder.handler.HelpClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.LoginButtonClickHandler;
import edu.arizona.sirls.etc.site.client.widget.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

public class LoggedOutHeaderBuilder implements IHeaderBuilder {

	private static LoggedOutHeaderBuilder instance;

	public static LoggedOutHeaderBuilder getInstance() {
		if(instance == null)
			instance = new LoggedOutHeaderBuilder();
		return instance;
	}
	
	private LoggedOutHeaderBuilder() { } 
	
	@Override
	public void build() {
		createHTML();
		insertWidgets();
	}

	private void createHTML() {
		Element header = DOM.getElementById("header");
		header.setInnerHTML("<div id='helpIcon'></div>" +
							"<div id='helpText'></div>" +
							"<div id='loginButton'></div>" +
							"<div id='loginButtonText'></div>" +
							"<div id='userField'></div>" +
							"<div id='passwordField'></div>" +
							"<div id='loginText'>User Login:</div>");
	}

	private void insertWidgets() {
		Button loginButton = new Button("Login");
		TextBox userField = new TextBox();
		PasswordTextBox passwordField = new PasswordTextBox();
		ImageLabelComposite helpImageLabelComposite = new ImageLabelComposite("images/Help.gif", "20px", "20px", "Help");
		
		RootPanel.get("userField").add(userField);
		RootPanel.get("passwordField").add(passwordField);
		RootPanel.get("loginButton").add(loginButton);
		RootPanel.get("helpIcon").add(helpImageLabelComposite);
		
		/** Style related; Override standard css of GWT and always apply these styles to textfield/button? */
		userField.setHeight("25px");
		passwordField.setHeight("25px");
		loginButton.setHeight("25px");
		loginButton.getElement().getStyle().setPaddingTop(2, Unit.PX);
		
				
		loginButton.addClickHandler(new LoginButtonClickHandler(userField, passwordField, loginButton, null, Session.getInstance().getPageBuilder()));
		helpImageLabelComposite.addClickHandler(new HelpClickHandler());
	}

}
