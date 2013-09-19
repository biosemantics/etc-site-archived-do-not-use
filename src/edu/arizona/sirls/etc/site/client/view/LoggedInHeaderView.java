package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import edu.arizona.sirls.etc.site.client.presenter.LoggedInHeaderPresenter;

public class LoggedInHeaderView extends Composite implements LoggedInHeaderPresenter.Display {

	private ImageLabelComposite helpImageLabelComposite = new ImageLabelComposite("images/Help.gif", "20px", "20px", "Help");
	private ImageLabelComposite homeImageLabelComposite = new ImageLabelComposite("images/Home.gif", "20px", "20px", "Home");
	private ImageLabelComposite taskManagerImageLabelComposite = new ImageLabelComposite("images/TaskManager.gif", "20px", "20px", "Task Manager");
	private ImageLabelComposite fileManagerImageLabelComposite = new ImageLabelComposite("images/FileManager.gif", "20px", "20px", "File Manager");
	private ImageLabelComposite settingsImageLabelComposite = new ImageLabelComposite("images/Settings.gif", "20px", "20px", "Settings");
	private Button logoutButton = new Button("Logout");
	private Label greetingLabel = new Label();

	public LoggedInHeaderView() { 
		HTMLPanel htmlPanel = new HTMLPanel(		
			"<div id='home'></div><div id='homeText'></div><div id='greetingText'></div>" +
			"<div id='taskManager'></div><div id='taskManagerText'></div>" +
			"<div id='fileManager'></div>" +
			"<div id='fileManagerText'></div>" +
			"<div id='settings'></div>" +
			"<div id='settingsText'></div>" +
			"<div id='logoutButton'></div>" +
			"<div id='logoutButtonText'></div>" +
			"<div class='helpIcon' id='helpIconLoggedIn'></div>" +
			"<div id='helpText'></div>");
		
		htmlPanel.add(greetingLabel, "greetingText");
		htmlPanel.add(helpImageLabelComposite, "helpIconLoggedIn");
		htmlPanel.add(homeImageLabelComposite, "home");
		htmlPanel.add(taskManagerImageLabelComposite, "taskManager");
		htmlPanel.add(fileManagerImageLabelComposite, "fileManager");
		htmlPanel.add(settingsImageLabelComposite, "settings");
		htmlPanel.add(logoutButton, "logoutButton");
	
		/** Style related; Override standard css of GWT and always apply these styles to textfield/button? */
		logoutButton.setHeight("25px");
		logoutButton.getElement().getStyle().setPaddingTop(2, Unit.PX);	
		
		this.initWidget(htmlPanel);
	}

	@Override
	public Label getGreetingLabel() {
		return this.greetingLabel;
	}

	@Override
	public Button getLogoutButton() {
		return this.logoutButton;
	}

	@Override
	public ImageLabelComposite getHelpImageLabelComposite() {
		return this.helpImageLabelComposite;
	}

	@Override
	public ImageLabelComposite getHomeImageLabelComposite() {
		return this.homeImageLabelComposite;
	}

	@Override
	public ImageLabelComposite getTaskManagerImageLabelComposite() {
		return this.taskManagerImageLabelComposite;
	}

	@Override
	public ImageLabelComposite getFileManagerImageLabelComposite() {
		return this.fileManagerImageLabelComposite;
	}

	@Override
	public ImageLabelComposite getSettingsImageLabelComposite() {
		return this.settingsImageLabelComposite;
	}
}
