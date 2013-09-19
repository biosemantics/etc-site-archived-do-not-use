package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ChangeAwareRichTextArea;
import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.PreprocessMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.Step;


public class PreprocessMatrixGenerationView extends MatrixGenerationView implements PreprocessMatrixGenerationPresenter.Display {

	private ChangeAwareRichTextArea textArea;
	private Button nextButton;
	private Button previousDescriptionButton;
	private Button nextDescriptionButton;
	private Label bracketCountsLabel;
	private Label descriptionIdLabel;

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("contentPanel");
		panel.add(new Label("Preprocess Text"));
		this.descriptionIdLabel = new Label();
		panel.add(descriptionIdLabel);
		this.bracketCountsLabel = new Label();
		panel.add(bracketCountsLabel);	
		this.textArea = new ChangeAwareRichTextArea();
		DOM.setElementAttribute(textArea.getElement(), "id", "preprocessTextArea");
		panel.add(textArea);
		HorizontalPanel navigationPanel = new HorizontalPanel();
		panel.add(navigationPanel);
		this.previousDescriptionButton = new Button("Previous Description");
		this.nextDescriptionButton = new Button("Next Description");
		navigationPanel.add(previousDescriptionButton);
		navigationPanel.add(nextDescriptionButton);
		this.nextButton = new Button("Next");
		navigationPanel.add(nextButton);
		return panel;
	}
	
	@Override
	public Button getNextButton() {
		return this.nextButton;
	}

	@Override
	public ChangeAwareRichTextArea getTextArea() {
		return this.textArea;
	}

	@Override
	protected Step getStep() {
		return Step.PREPROCESS_TEXT;
	}

	@Override
	public Button getPreviousDescriptionButton() {
		return this.previousDescriptionButton;
	}

	@Override
	public Button getNextDescriptionButton() {
		return this.nextDescriptionButton;
	}

	@Override
	public Label getBracketCountsLabel() {
		return this.bracketCountsLabel;
	}

	@Override
	public Label getDescriptionIDLabel() {
		return this.descriptionIdLabel;
	}
	
}
