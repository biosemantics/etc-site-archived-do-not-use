package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

public class CloseDialogBoxClickHandler implements ClickHandler {

	private TitleCloseDialogBox dialogBox;
	
	public CloseDialogBoxClickHandler() { }

	public CloseDialogBoxClickHandler(TitleCloseDialogBox dialogBox) {
		super();
		this.dialogBox = dialogBox;
	}

	public void setDialogBox(TitleCloseDialogBox dialogBox) {
		this.dialogBox = dialogBox;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		this.dialogBox.hide();
	}

}
