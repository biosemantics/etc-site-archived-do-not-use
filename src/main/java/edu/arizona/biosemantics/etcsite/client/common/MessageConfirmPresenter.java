package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView.IConfirmListener;

public class MessageConfirmPresenter implements IMessageConfirmView.Presenter {

	private IMessageConfirmView view;
	private PopupPanel dialogBox;
	private IConfirmListener currentListener;
	
	@Inject
	public MessageConfirmPresenter(IMessageConfirmView view) {
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		this.view = view;
		view.setPresenter(this);
	}
	
	@Override
	public void show(String title, String message, String cancelText, String confirmText,
			IConfirmListener listener) {
		this.currentListener = listener;
		view.setHtmlMessage(message);
		view.setConfirmText(confirmText);
		view.setCancelText(cancelText);
		dialogBox.center();
	}
	
	@Override
	public void show(String title, String message, IConfirmListener listener) {
		this.currentListener = listener;
		view.setHtmlMessage(message);
		dialogBox.center();
	}
	
	@Override
	public void onConfirm() {
		dialogBox.hide();
		if(currentListener != null)
			currentListener.onConfirm();
	}

	@Override
	public void onCancel() {
		dialogBox.hide();
		if(currentListener != null)
			currentListener.onCancel();
	}
	
	public abstract static class AbstractConfirmListener implements IConfirmListener {
		public void onCancel() { }
	}
}
