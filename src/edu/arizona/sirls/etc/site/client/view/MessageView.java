package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import  edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;

public class MessageView extends Composite implements MessagePresenter.Display {

	private Button closeButton;
	private HTML messageHTML;

	public MessageView() {
		messageHTML = new HTML();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("dialogVPanel");
		verticalPanel.add(messageHTML);
		verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		closeButton = new Button("Close");
		verticalPanel.add(closeButton);
		this.initWidget(verticalPanel);
	}

	@Override
	public Button getCloseButton() {
		return this.closeButton;
	}

	@Override
	public HTML getMessageHTML() {
		return this.messageHTML;
	}
	
}
