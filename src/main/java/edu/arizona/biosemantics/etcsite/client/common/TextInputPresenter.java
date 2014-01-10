package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.ICancelConfirmHandler;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

public class TextInputPresenter implements ITextInputView.Presenter {

	private ITextInputView view;
	private TitleCloseDialogBox dialogBox;
	private ITextInputListener currentListener;

	@Inject
	public TextInputPresenter(final ITextInputView view) {
		this.view = view;
		view.setPresenter(this);
		this.dialogBox = new TitleCloseDialogBox(false, "");
		this.dialogBox.setAnimationEnabled(true);
		dialogBox.setWidget(view);
		dialogBox.setCancelConfirmHandler(new ICancelConfirmHandler() {
			@Override
			public void cancel() {
				onCancel();
			}
			@Override
			public void confirm() {
				onConfirm(view.getTextBox());
			}
		});
	}
	
	@Override
	public void show(String title, String text, String defaultTextBoxText, ITextInputListener listener) {
		this.currentListener = listener;
		dialogBox.setTitle(title);
		view.setText(text);
		view.setTextBox(defaultTextBoxText);
		dialogBox.center();	
	}

	@Override
	public void onConfirm(String text) {
		dialogBox.hide();
		currentListener.onConfirm(text);
	}

	@Override
	public void onCancel() {
		dialogBox.hide();
		currentListener.onCancel();
	}
}
