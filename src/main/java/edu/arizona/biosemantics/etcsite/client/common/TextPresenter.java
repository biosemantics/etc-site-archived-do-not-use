package edu.arizona.biosemantics.etcsite.client.common;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;

public class TextPresenter {

	private Dialog dialog;
	private TextView view;
	
	@Inject
	public TextPresenter(TextView view) { 
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Register");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setPredefinedButtons();
		dialog.add(view.asWidget());
		this.view = view;
		view.setPresenter(this);
	}
	
	public void showMessage(String title, String message) {
		dialog.setTitle(title);
		view.setHtml(message);
		dialog.center();
	}

	public void onClose() {
		dialog.hide();
	}
	
}
