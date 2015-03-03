package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class TextView extends Composite {

	private static MessageViewUiBinder uiBinder = GWT.create(MessageViewUiBinder.class);

	interface MessageViewUiBinder extends UiBinder<Widget, TextView> {
	}

	public TextView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	TextArea textArea;
	
	@UiField
	Button closeButton;
	
	private TextPresenter presenter;
	
	@UiHandler("closeButton")
	public void onClose(ClickEvent event) {
		presenter.onClose();
	}
	
	public void setHtml(String html) {
		//messageHTML.setHTML(html);
		textArea.setText(html);
	}
	
	public void setPresenter(TextPresenter presenter) {
		this.presenter = presenter;
	}

}
