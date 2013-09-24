package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.LearnMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.view.ListItem;
import edu.arizona.sirls.etc.site.client.view.OrderedList;
import edu.arizona.sirls.etc.site.shared.rpc.Step;

public class LearnMatrixGenerationView extends MatrixGenerationView implements LearnMatrixGenerationPresenter.Display {
	
	private Label sentencesLabel;
	private Label wordsLabel;
	private Button nextButton;

	@Override
	protected Step getStep() {
		return Step.LEARN_TERMS;
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
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Image infoImage = new Image("images/Info.gif");
		infoImage.addStyleName("infoImage");
		horizontalPanel.add(infoImage);
		horizontalPanel.add(new Label("We are now learning the terminology. " +
				"You will receive an email when processing has completed. You can come back to this task using the Task Manager."));
		panel.add(horizontalPanel);

		//only for design purpose, until we have a fake process
		this.nextButton = new Button("Next");
		panel.add(nextButton);
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
	public Button getNextButton() {
		return this.nextButton;
	}

}
