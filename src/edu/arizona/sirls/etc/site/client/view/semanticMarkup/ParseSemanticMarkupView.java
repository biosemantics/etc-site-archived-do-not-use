package edu.arizona.sirls.etc.site.client.view.semanticMarkup;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.TaskStageEnum;
import edu.arizona.sirls.etc.site.client.presenter.semanticMarkup.ParseSemanticMarkupPresenter;

public class ParseSemanticMarkupView extends SemanticMarkupView implements ParseSemanticMarkupPresenter.Display {

	private Button nextButton;
	private Anchor taskManagerAnchor;
	private HorizontalPanel statusPanel;
	private FlowPanel taskManagerFlowPanel;
	private Image statusImage;

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.PARSE_TEXT;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		
		statusPanel = new HorizontalPanel();
		statusImage = new Image("images/loader3.gif");
		statusImage.addStyleName("infoImage");
		statusPanel.add(statusImage);
		taskManagerFlowPanel = new FlowPanel();
		statusPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are now parsing the descriptions. " +
				"You will receive an email when processing has completed. You can come back to this task using the "));
		taskManagerAnchor = new Anchor("Task Manager");
		taskManagerFlowPanel.add(taskManagerAnchor);
		taskManagerFlowPanel.add(new InlineLabel("."));
		panel.add(statusPanel);
		
		this.nextButton = new Button("Next");
		panel.add(nextButton);
		return panel;
	}

	@Override
	public Anchor getTaskManagerAnchor() {
		return this.taskManagerAnchor;
	}
	
	@Override
	public void setResumableStatus() {
		this.nextButton.setVisible(true);
		statusPanel.clear();
		statusImage = new Image("images/play.png");
		statusImage.addStyleName("clickable");
		statusImage.addStyleName("infoImage");
		statusPanel.add(statusImage);
		taskManagerFlowPanel = new FlowPanel();
		statusPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are done parsing the descriptions."));
	}
	
	@Override
	public HasClickHandlers getResumableClickable() {
		return statusImage;
	}

	@Override
	public void setNonResumableStatus() {
		this.nextButton.setVisible(false);
		statusPanel.clear();
		statusImage = new Image("images/loader3.gif");
		statusImage.addStyleName("infoImage");
		statusPanel.add(statusImage);
		taskManagerFlowPanel = new FlowPanel();
		statusPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are now parsing the descriptions. " +
				"You will receive an email when processing has completed. You can come back to this task using the "));
		taskManagerAnchor = new Anchor("Task Manager");
		taskManagerFlowPanel.add(taskManagerAnchor);
		taskManagerFlowPanel.add(new InlineLabel("."));
	}

	
	@Override
	public Button getNextButton() {
		return this.nextButton;
	}
}
