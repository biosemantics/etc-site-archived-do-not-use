package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import  edu.arizona.sirls.etc.site.client.presenter.MessageResumeOrStartPresenter;

public class MessageResumeOrStartView extends Composite implements MessageResumeOrStartPresenter.Display {

	private Button resumeButton;
	private Button startButton;
	private HTML messageHTML;

	public MessageResumeOrStartView() {
		messageHTML = new HTML();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("dialogVPanel");
		verticalPanel.add(messageHTML);
		verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		HorizontalPanel navigationPanel = new HorizontalPanel();
		resumeButton = new Button("Resume");
		navigationPanel.add(resumeButton);
		startButton = new Button("Start");
		navigationPanel.add(startButton);
		verticalPanel.add(navigationPanel);
		this.initWidget(verticalPanel);
	}

	@Override
	public HTML getMessageHTML() {
		return this.messageHTML;
	}

	@Override
	public Button getResumeButton() {
		return this.resumeButton;
	}

	@Override
	public Button getStartButton() {
		return this.startButton;
	}
	
}
