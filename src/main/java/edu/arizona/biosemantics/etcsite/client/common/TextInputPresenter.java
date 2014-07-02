package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ITextInputView.ITextInputListener;

public class TextInputPresenter implements ITextInputView.Presenter {

	private ITextInputView view;
	private PopupPanel dialogBox;
	private ITextInputListener currentListener;

	@Inject
	public TextInputPresenter(final ITextInputView view) {
		this.view = view;
		view.setPresenter(this);
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
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
		this.confirm(text);
	}
	
	@Override
	public void onCancel() {
		dialogBox.hide();		
	}
	
	private void confirm(String text) {
		currentListener.onConfirm(text);
	}
	
	private void cancel() {
		currentListener.onCancel();
	}

}
