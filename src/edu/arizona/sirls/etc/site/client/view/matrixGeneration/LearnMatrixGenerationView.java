package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.LearnMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class LearnMatrixGenerationView extends MatrixGenerationView implements LearnMatrixGenerationPresenter.Display {
	
	private Label sentencesLabel;
	private Label wordsLabel;
	//private Button nextButton;
	private Anchor taskManagerAnchor;
	private HorizontalPanel statusPanel;
	private FlowPanel taskManagerFlowPanel;
	private Image statusImage;

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.LEARN_TERMS;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("contentPanel");
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new InlineLabel("There are "));
		wordsLabel = new InlineLabel("x words");
		sentencesLabel = new InlineLabel("x sentences");
		wordsLabel.addStyleName("highlightGreen");
		sentencesLabel.addStyleName("highlightGreen");
		flowPanel.add(wordsLabel);
		flowPanel.add(new InlineLabel(" and "));
		flowPanel.add(sentencesLabel);
		flowPanel.add(new InlineLabel(" in this collection of descriptions."));
		
		panel.add(flowPanel);
		
		statusPanel = new HorizontalPanel();
		statusImage = new Image("images/loader3.gif");
		statusImage.addStyleName("infoImage");
		statusPanel.add(statusImage);
		taskManagerFlowPanel = new FlowPanel();
		statusPanel.add(taskManagerFlowPanel);
		taskManagerFlowPanel.add(new InlineLabel("We are now learning the terminology. " +
				"You will receive an email when processing has completed. You can come back to this task using the "));
		taskManagerAnchor = new Anchor("Task Manager");
		taskManagerFlowPanel.add(taskManagerAnchor);
		taskManagerFlowPanel.add(new InlineLabel("."));
		panel.add(statusPanel);

		//only for design purpose, until we have a fake process
		//this.nextButton = new Button("Next");
		//panel.add(nextButton);
		return panel;
	}

	@Override
	public void setSentences(int sentences) {
		this.sentencesLabel.setText(sentences + " sentences");
	}

	@Override
	public void setWords(int words) {
		this.wordsLabel.setText(words + " words");
	}

	@Override
	public Anchor getTaskManagerAnchor() {
		return this.taskManagerAnchor;
	}

	@Override
	public void setResumableStatus() {
		statusPanel.clear();
		statusImage = new Image("images/play.png");
		statusImage.addStyleName("infoImage");
		statusPanel.add(statusImage);
		statusPanel.add(taskManagerFlowPanel);
	}
	
	@Override
	public HasClickHandlers getResumableClickable() {
		return statusImage;
	}

	@Override
	public void setNonResumableStatus() {
		statusImage = new Image("images/loader3.gif");
		statusImage.addStyleName("infoImage");
	}
	
	/*@Override
	public Button getNextButton() {
		return this.nextButton;
	}*/

}
