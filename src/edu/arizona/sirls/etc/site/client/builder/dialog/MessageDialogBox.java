package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageDialogBox extends DialogBox {

	private HTML responseLabel = new HTML();
	private Button closeButton = new Button("Close");

	public MessageDialogBox(String title) { 
		this.setText(title);
		this.setAnimationEnabled(true);
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(responseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		this.setWidget(dialogVPanel);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MessageDialogBox.this.hide();
			}
		}); 
	}
	
	public void setResponse(String html) {
		this.responseLabel.setHTML(html);
	}

	public void setCloseButtonFocus(boolean b) {
		this.closeButton.setFocus(true);
	}
	
}
