package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ParseMatrixGenerationPresenter;

public class ParseMatrixGenerationView extends MatrixGenerationView implements ParseMatrixGenerationPresenter.Display {

	//private Button nextButton;

	private Anchor taskManagerAnchor;

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.PARSE_TEXT;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Image infoImage = new Image("images/Info.gif");
		infoImage.addStyleName("infoImage");
		horizontalPanel.add(infoImage);
		FlowPanel taskManagerFlowPanel = new FlowPanel();
		horizontalPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are now parsing the descriptions. " +
				"You will receive an email when processing has completed. You can come back to this task using the "));
		taskManagerAnchor = new Anchor("Task Manager");
		taskManagerFlowPanel.add(taskManagerAnchor);
		taskManagerFlowPanel.add(new InlineLabel("."));
		panel.add(horizontalPanel);
		
		//only while we don't use real tasks/face process
		//this.nextButton = new Button("Next");
		//panel.add(nextButton);
		return panel;
	}

	@Override
	public Anchor getTaskManagerAnchor() {
		return this.taskManagerAnchor;
	}

	/*
	@Override
	public Button getNextButton() {
		return this.nextButton;
	}*/
}
