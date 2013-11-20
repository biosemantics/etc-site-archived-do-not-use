package edu.arizona.sirls.etc.site.client.view.semanticMarkup;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.semanticMarkup.ChangeAwareRichTextArea;
import edu.arizona.sirls.etc.site.client.presenter.semanticMarkup.PreprocessSemanticMarkupPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.TaskStageEnum;


public class PreprocessSemanticMarkupView extends SemanticMarkupView implements PreprocessSemanticMarkupPresenter.Display {

	private ChangeAwareRichTextArea textArea;
	private Button nextButton;
	private Button previousDescriptionButton;
	private Button nextDescriptionButton;
	private HTML bracketCountsHTML;
	private Label descriptionIdLabel;

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("contentPanel");
		
		panel.add(new Label("We found unmatched brackets in the descriptions you provided. " +
				"Please take the time to review the descriptions for which this is the case."));
		HorizontalPanel filePanel = new HorizontalPanel();
		filePanel.add(new Label());
		this.descriptionIdLabel = new Label();
		filePanel.add(descriptionIdLabel);
		panel.add(filePanel);
		
		HorizontalPanel countsPanel = new HorizontalPanel();
		countsPanel.add(new Label("Bracket counts: "));
		this.bracketCountsHTML = new HTML();
		bracketCountsHTML.addStyleName("bracketCounts");
		countsPanel.add(bracketCountsHTML);
		panel.add(countsPanel);
		
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
	protected TaskStageEnum getStep() {
		return TaskStageEnum.PREPROCESS_TEXT;
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
	public HTML getBracketCountsHTML() {
		return this.bracketCountsHTML;
	}

	@Override
	public Label getDescriptionIDLabel() {
		return this.descriptionIdLabel;
	}
	
}
