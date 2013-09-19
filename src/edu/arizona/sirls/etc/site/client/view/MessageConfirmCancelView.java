package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import  edu.arizona.sirls.etc.site.client.presenter.MessageConfirmCancelPresenter;

public class MessageConfirmCancelView extends Composite implements MessageConfirmCancelPresenter.Display {

	private Button cancelButton;
	private Button confirmButton;
	private HTML messageHTML;

	public MessageConfirmCancelView() {
		messageHTML = new HTML();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("dialogVPanel");
		verticalPanel.add(messageHTML);
		verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		HorizontalPanel navigationPanel = new HorizontalPanel();
		cancelButton = new Button("Cancel");
		navigationPanel.add(cancelButton);
		confirmButton = new Button("Confirm");
		navigationPanel.add(confirmButton);
		verticalPanel.add(navigationPanel);
		this.initWidget(verticalPanel);
	}

	@Override
	public HTML getMessageHTML() {
		return this.messageHTML;
	}

	@Override
	public Button getConfirmButton() {
		return this.confirmButton;
	}

	@Override
	public Button getCancelButton() {
		return this.cancelButton;
	}
	
}
