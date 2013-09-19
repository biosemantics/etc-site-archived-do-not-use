package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.Step;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ReviewMatrixGenerationPresenter;

public class ReviewMatrixGenerationView extends MatrixGenerationView implements ReviewMatrixGenerationPresenter.Display {

	private Anchor otoAnchor;
	private Button nextButton;

	@Override
	protected Step getStep() {
		return Step.REVIEW_TERMS;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		panel.add(new Label("Review Terms"));
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new InlineLabel("Please review the terms learned by "));
		this.otoAnchor = new Anchor("visiting OTO");
		flowPanel.add(otoAnchor);
		panel.add(flowPanel);
		this.nextButton = new Button("Next");
		panel.add(nextButton);
		return panel;
	}

	@Override
	public Anchor getOtoAnchor() {
		return this.otoAnchor;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}
}
