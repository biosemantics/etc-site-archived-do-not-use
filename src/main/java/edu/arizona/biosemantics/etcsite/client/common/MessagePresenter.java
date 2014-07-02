package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.IMessageView.Presenter;

public class MessagePresenter implements Presenter {

	private PopupPanel dialogBox;
	private IMessageView view;
	
	@Inject
	public MessagePresenter(IMessageView view) { 
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		this.view = view;
		view.setPresenter(this);
	}
	
	@Override
	public void showMessage(String title, String message) {
		view.setHtml(message);
		dialogBox.center();
	}

	@Override
	public void onClose() {
		dialogBox.hide();
	}
	
}
