package edu.arizona.sirls.etc.site.client.view.semanticMarkup;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.semanticMarkup.ReviewSemanticMarkupPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.TaskStageEnum;

public class ReviewSemanticMarkupView extends SemanticMarkupView implements ReviewSemanticMarkupPresenter.Display {

	private Button nextButton;
	private Frame frame;

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.REVIEW_TERMS;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		
		panel.add(new Label("Please review the terms we learned. " +
				"You can select terms in the left column and drag them into the categories on the right hand side."));
		frame = new Frame();
		frame.getElement().setAttribute("frameBorder", "0");
		panel.add(frame);
		frame.addStyleName("otoFrame");
		
		//only until online and we have found a way to register for the click event on the oto iframe 'finalize'
		this.nextButton = new Button("Next");
		panel.add(nextButton);
		return panel;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}
	
	public Frame getFrame() {
		return frame;
	}
}
