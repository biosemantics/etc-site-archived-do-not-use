package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LabelTextFieldDialogBox extends TitleCloseDialogBox {

	private Label label = new Label();
	private TextBox textBox = new TextBox();
	private Button cancelButton = new Button("Cancel");
	private Button confirmButton = new Button("Confirm");

	public LabelTextFieldDialogBox(String title, String labelText, String defaultTextBoxText,  final ILabelTextFieldDialogBoxHandler handler) { 
		super(title);
		this.label.setText(labelText);
		this.setAnimationEnabled(true);
		this.textBox.setText(defaultTextBoxText);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(label);
		verticalPanel.add(textBox);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(cancelButton);
		horizontalPanel.add(confirmButton);
		verticalPanel.add(horizontalPanel);
		
		this.setWidget(verticalPanel);
		
		// Add a handler to close the DialogBox
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LabelTextFieldDialogBox.this.hide();
				handler.canceled();
			}
		}); 
		
		confirmButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LabelTextFieldDialogBox.this.hide();
				handler.confirmed(textBox.getText());
			}
		}); 
	}	
	
}
