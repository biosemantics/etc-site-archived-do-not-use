package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.IMessageView.Presenter;

public class MessagePresenter implements Presenter {

	private TitleCloseDialogBox dialogBox;
	private IMessageView view;
	
	@Inject
	public MessagePresenter(IMessageView view) { 
		this.dialogBox = new TitleCloseDialogBox(false, ""); 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		this.view = view;
		view.setPresenter(this);
	}
	
	@Override
	public void showMessage(String title, String message) {
		dialogBox.setTitle(title);
		view.setHtml(message);
		dialogBox.center();
	}

	@Override
	public void onClose() {
		dialogBox.hide();
	}
	
}
