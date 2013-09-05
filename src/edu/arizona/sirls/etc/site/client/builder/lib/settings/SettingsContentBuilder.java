package edu.arizona.sirls.etc.site.client.builder.lib.settings;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;

public class SettingsContentBuilder implements IContentBuilder {
	
	private Label oldPasswordLabel = new Label("Old Password");
	private PasswordTextBox oldPasswordTextBox = new PasswordTextBox();
	private Label newPasswordLabel = new Label("New Password");
	private PasswordTextBox newPasswordTextBox = new PasswordTextBox();
	private Button submitButton = new Button("Submit");
	
	@Override
	public void build() {
		createHTML();
		initWidgets();
	}

	private void initWidgets() {
		RootPanel settingsContent = RootPanel.get("settingsContent");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(new Label("Change your password"));
		FlexTable flexTable = new FlexTable();
		flexTable.setWidget(0, 0, oldPasswordLabel);
		flexTable.setWidget(0,  1, oldPasswordTextBox);
		flexTable.setWidget(1, 0, newPasswordLabel);
		flexTable.setWidget(1, 1, newPasswordTextBox);
		flexTable.setWidget(2, 1, submitButton);
		verticalPanel.add(flexTable);
		settingsContent.add(verticalPanel);
		
		submitButton.addClickHandler(new SubmitSettingsClickHandler(oldPasswordTextBox, newPasswordTextBox));
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'><div id='settingsContent'></div></div>");
	}

}
