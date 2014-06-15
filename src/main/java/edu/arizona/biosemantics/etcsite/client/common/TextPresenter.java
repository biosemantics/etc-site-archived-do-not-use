package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.IMessageView.Presenter;

public class TextPresenter {

	private TitleCloseDialogBox dialogBox;
	//private DialogBox dialogBox;
	private TextView view;
	
	@Inject
	public TextPresenter(TextView view) { 
		this.dialogBox = new TitleCloseDialogBox(false, ""); 
		//this.dialogBox = new DialogBox(false); 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		this.view = view;
		view.setPresenter(this);
	}
	
	public void showMessage(String title, String message) {
		dialogBox.setText(title);
		view.setHtml(message);
		dialogBox.center();
	}

	public void onClose() {
		dialogBox.hide();
	}
	
}
