package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessageOkView extends Composite implements IMessageOkView {

	private static MessageOkViewUiBinder uiBinder = GWT.create(MessageOkViewUiBinder.class);

	interface MessageOkViewUiBinder extends UiBinder<Widget, MessageOkView> {
	}
	
	private Presenter presenter;

	public MessageOkView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField
	Label label;

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("okButton")
	public void onOk(ClickEvent event) {
		presenter.onOk();
	}
	
	@Override
	public void setMessage(String message){
		label.setText(message);
	}
}
