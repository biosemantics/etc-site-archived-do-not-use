package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.IHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.handler.FileManagerClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.HelpClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.HomeClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.LoginButtonClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.LogoutButtonClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.SettingsClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.TaskManagerClickHandler;
import edu.arizona.sirls.etc.site.client.widget.ImageLabelComposite;

import com.google.gwt.safehtml.shared.UriUtils;

public class LoggedInHeaderBuilder implements IHeaderBuilder {

	private static LoggedInHeaderBuilder instance;

	public static LoggedInHeaderBuilder getInstance() {
		if(instance == null)
			instance = new LoggedInHeaderBuilder();
		return instance;
	}
	
	private LoggedInHeaderBuilder() { } 
	
	@Override
	public void build() {
		createHTML();
		insertWidgets();
	}

	private void createHTML() {
		Element header = DOM.getElementById("header");
		header.setInnerHTML(
				"<div id='home'></div><div id='homeText'></div><div id='greetingText'></div>" +
				"<div id='taskManager'></div><div id='taskManagerText'></div>" +
				"<div id='fileManager'></div>" +
				"<div id='fileManagerText'></div>" +
				"<div id='settings'></div>" +
				"<div id='settingsText'></div>" +
				"<div id='logoutButton'></div>" +
				"<div id='logoutButtonText'></div>" +
				"<div id='helpIcon'></div>" +
				"<div id='helpText'></div>");
	}

	private void insertWidgets() {
		String username = Authentication.getInstance().getUsername();
		Label greetingLabel = new Label();
		if(username != null)
			greetingLabel.setText("Hello " + username + ":");
		
		Button logoutButton = new Button("Logout");
		/*Label helpLabel = new Label("Help");
		helpLabel.addStyleName("helpLabel");
		Label homeLabel = new Label("Home");
		homeLabel.addStyleName("homeLabel");
		Label taskManagerLabel = new Label("Task Manager");
		taskManagerLabel.addStyleName("taskManagerLabel");
		Label fileManagerLabel = new Label("File Manager");
		fileManagerLabel.addStyleName("fileManagerLabel");
		Label settingsLabel = new Label("Settings");
		settingsLabel.addStyleName("settingsLabel");
		*/
		ImageLabelComposite helpImageLabelComposite = new ImageLabelComposite("images/Help.gif", "20px", "20px", "Help");
		ImageLabelComposite homeImageLabelComposite = new ImageLabelComposite("images/Home.gif", "20px", "20px", "Home");
		ImageLabelComposite taskManagerImageLabelComposite = new ImageLabelComposite("images/TaskManager.gif", "20px", "20px", "Task Manager");
		ImageLabelComposite fileManagerImageLabelComposite = new ImageLabelComposite("images/FileManager.gif", "20px", "20px", "File Manager");
		ImageLabelComposite settingsImageLabelComposite = new ImageLabelComposite("images/Settings.gif", "20px", "20px", "Settings");
		
		RootPanel.get("greetingText").add(greetingLabel);
		RootPanel.get("logoutButton").add(logoutButton);
		RootPanel.get("helpIcon").add(helpImageLabelComposite);
		RootPanel.get("home").add(homeImageLabelComposite);
		RootPanel.get("taskManager").add(taskManagerImageLabelComposite);
		RootPanel.get("fileManager").add(fileManagerImageLabelComposite);
		RootPanel.get("settings").add(settingsImageLabelComposite);
		
		/** Style related; Override standard css of GWT and always apply these styles to textfield/button? */
		logoutButton.setHeight("25px");
		logoutButton.getElement().getStyle().setPaddingTop(2, Unit.PX);	
		logoutButton.addClickHandler(new LogoutButtonClickHandler());
		helpImageLabelComposite.addClickHandler(new HelpClickHandler());
		homeImageLabelComposite.addClickHandler(new HomeClickHandler());
		taskManagerImageLabelComposite.addClickHandler(new TaskManagerClickHandler());
		fileManagerImageLabelComposite.addClickHandler(new FileManagerClickHandler());
		settingsImageLabelComposite.addClickHandler(new SettingsClickHandler());
	}

}
