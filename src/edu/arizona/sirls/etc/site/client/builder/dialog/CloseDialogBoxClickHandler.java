package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;

public class CloseDialogBoxClickHandler implements ClickHandler {

	private DialogBox dialogBox;
	
	public CloseDialogBoxClickHandler() { }

	public CloseDialogBoxClickHandler(DialogBox dialogBox) {
		super();
		this.dialogBox = dialogBox;
	}

	public void setDialogBox(DialogBox dialogBox) {
		this.dialogBox = dialogBox;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		this.dialogBox.hide();
	}

}
