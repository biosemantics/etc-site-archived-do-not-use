package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

public class LabelTextFieldPresenter {

	public interface Display {
		Label getLabel();
		TextBox getTextBox();
		Button getCancelButton();
		Button getConfirmButton();
		Widget asWidget();
	}

	private Display display;
	private TitleCloseDialogBox dialogBox;
	private ILabelTextFieldDialogBoxHandler handler;
	private String defaultTextBoxText;
	private String labelText;
	
	public LabelTextFieldPresenter(Display display, String title, 
			String labelText, String defaultTextBoxText, ILabelTextFieldDialogBoxHandler handler) {
		this.display = display;
		this.dialogBox = new TitleCloseDialogBox(false, title);
		this.labelText = labelText;
		this.defaultTextBoxText = defaultTextBoxText;
		this.handler = handler;
		bind();
	}

	private void bind() {
		display.getLabel().setText(labelText);
		display.getTextBox().setText(defaultTextBoxText);
		
		// Add a handler to close the DialogBox
		display.getCancelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				handler.canceled();
			}
		}); 
		
		display.getConfirmButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				handler.confirmed(display.getTextBox().getText());
			}
		}); 
	}

	public void go() { 
		dialogBox.clear();
		dialogBox.setAnimationEnabled(true);
		dialogBox.add(display.asWidget());
		dialogBox.center();	
	}
}
