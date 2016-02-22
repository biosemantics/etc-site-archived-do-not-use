package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextInputView extends Composite implements ITextInputView {

	private static TextInputViewUiBinder uiBinder = GWT.create(TextInputViewUiBinder.class);

	interface TextInputViewUiBinder extends UiBinder<Widget, TextInputView> {
	}

	public TextInputView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button cancelButton;

	@UiField
	Button confirmButton;
	
	@UiField
	TextBox textBox;
	
	@UiField
	Label label;

	private Presenter presenter;

	@UiHandler("confirmButton")
	public void onConfirm(ClickEvent event) {
		presenter.onConfirm(textBox.getText());
	}
	
	@UiHandler("cancelButton") 
	public void onCancel(ClickEvent event) {
		presenter.onCancel();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setText(String text) {
		this.label.setText(text);
	}

	@Override
	public void setTextBox(String text) {
		this.textBox.setText(text);
	}

	@Override
	public String getTextBox() {
		return this.textBox.getText();
	}
}
