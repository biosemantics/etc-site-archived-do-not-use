package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.Step;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ParseMatrixGenerationPresenter;

public class ParseMatrixGenerationView extends MatrixGenerationView implements ParseMatrixGenerationPresenter.Display {

	private Button nextButton;

	@Override
	protected Step getStep() {
		return Step.PARSE_TEXT;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("contentPanel");
		panel.add(new Label("Parse Text"));
		panel.add(new Label("The system is parsing text."));
		panel.add(new Label("When the process is completed you will receive an email."));
		
		this.nextButton = new Button("Next");
		panel.add(nextButton);
		return panel;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}
}
