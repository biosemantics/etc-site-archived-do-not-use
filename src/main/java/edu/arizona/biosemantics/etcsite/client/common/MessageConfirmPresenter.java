package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

public class MessageConfirmPresenter implements IMessageConfirmView.Presenter {

	private IMessageConfirmView view;
	private TitleCloseDialogBox dialogBox;
	private IConfirmListener currentListener;
	
	@Inject
	public MessageConfirmPresenter(IMessageConfirmView view) {
		this.dialogBox = new TitleCloseDialogBox(false, ""); 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		this.view = view;
		view.setPresenter(this);
	}
	
	@Override
	public void show(String title, String message, String cancelText, String confirmText,
			IConfirmListener listener) {
		this.currentListener = listener;
		dialogBox.setTitle(title);
		view.setHtmlMessage(message);
		view.setConfirmText(confirmText);
		view.setCancelText(cancelText);
		dialogBox.center();
	}
	
	@Override
	public void show(String title, String message, IConfirmListener listener) {
		this.currentListener = listener;
		dialogBox.setTitle(title);
		view.setHtmlMessage(message);
		dialogBox.center();
	}
	
	@Override
	public void onConfirm() {
		if(currentListener != null)
			currentListener.onConfirm();
		dialogBox.hide();
	}

	@Override
	public void onCancel() {
		dialogBox.hide();
	}

	public interface IConfirmListener {
		public void onConfirm();
		public void onCancel();
	}
	
	public abstract static class AbstractConfirmListener implements IConfirmListener {
		public void onCancel() { }
	}
}
