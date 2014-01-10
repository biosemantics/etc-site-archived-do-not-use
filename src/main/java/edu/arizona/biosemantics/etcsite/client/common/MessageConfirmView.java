package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MessageConfirmView extends Composite implements IMessageConfirmView {

	private static MessageConfirmViewUiBinder uiBinder = GWT.create(MessageConfirmViewUiBinder.class);

	interface MessageConfirmViewUiBinder extends UiBinder<Widget, MessageConfirmView> {
	}

	@UiField
	HTML messageHTML;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button confirmButton;

	private Presenter presenter;
	
	public MessageConfirmView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.onCancel();
	}
	
	@UiHandler("confirmButton")
	public void onConfirm(ClickEvent event) {
		presenter.onConfirm();
	}
	
	@Override
	public void setHtmlMessage(String htmlMessage) {
		messageHTML.setHTML(htmlMessage);
	}	
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setConfirmText(String confirmText) {
		this.confirmButton.setText(confirmText);
	}

	@Override
	public void setCancelText(String cancelText) {
		this.cancelButton.setText(cancelText);
	}

}
