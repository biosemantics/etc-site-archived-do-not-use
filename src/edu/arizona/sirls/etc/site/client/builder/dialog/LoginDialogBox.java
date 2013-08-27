package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.handler.LoginButtonClickHandler;

public class LoginDialogBox extends DialogBox {

	private Label messageLabel = new Label();
	private Label passwordLabel = new Label("Password:");
	private Label userNameLabel = new Label("User name:");
	private Button loginButton = new Button("Login");
	private Button cancelButton = new Button("Cancel");
	private TextBox userNameField = new TextBox();
	private PasswordTextBox passwordField = new PasswordTextBox();

	public LoginDialogBox(String message, PageBuilder targetPageBuilder) { 
		this.setText("Login");
		this.setAnimationEnabled(true);
		
		FlexTable t = new FlexTable();
		messageLabel.setText(message);
		t.setWidget(0, 0, messageLabel);
		t.getFlexCellFormatter().setColSpan(0, 0, 2);
		t.setWidget(1, 0, userNameLabel);
		t.setWidget(1, 1, userNameField);
		t.setWidget(2, 0, passwordLabel);
		t.setWidget(2, 1, passwordField);
		t.setWidget(3, 0, cancelButton);
		t.setWidget(3, 1, loginButton);

	    // ...and set it's column span so that it takes up the whole row.
	    //t.getFlexCellFormatter().setColSpan(1, 0, 3);

		
		/*VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(responseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		this.setWidget(dialogVPanel);*/
		this.setWidget(t);
		
		// Add a handler to close the DialogBox
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LoginDialogBox.this.hide();
			}
		}); 
		
		loginButton.addClickHandler(new LoginButtonClickHandler(userNameField, passwordField, loginButton, this, targetPageBuilder));
	}
	
}
