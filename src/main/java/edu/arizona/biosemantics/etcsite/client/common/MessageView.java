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

public class MessageView extends Composite implements IMessageView {

	private static MessageViewUiBinder uiBinder = GWT.create(MessageViewUiBinder.class);

	interface MessageViewUiBinder extends UiBinder<Widget, MessageView> {
	}

	public MessageView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTML messageHTML;
	
	@UiField
	Button closeButton;
	
	private Presenter presenter;
	
	@UiHandler("closeButton")
	public void onClose(ClickEvent event) {
		presenter.onClose();
	}
	
	@Override
	public void setHtml(String html) {
		messageHTML.setHTML(html);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
