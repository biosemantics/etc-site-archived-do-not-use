package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.InputMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class InputMatrixGenerationView extends MatrixGenerationView implements 
		InputMatrixGenerationPresenter.Display {

	private Label taxonDescriptionFileNameLabel;
	private Button nextButton;
	private FocusWidget taxonDescriptionFileButton;
	private Anchor formatRequirementsAnchor;
	private ListBox glossaryListBox;
	private FocusWidget fileManagerAnchor;
	private TextBox nameTextBox;

	@Override
	protected Widget getStepWidget() {
		this.taxonDescriptionFileNameLabel = new Label();
		this.nextButton = new Button("Next");
		this.glossaryListBox = new ListBox();
		glossaryListBox.setVisibleItemCount(1);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("contentPanel");
		FlowPanel inputRequirementsPanel = new FlowPanel(); 
		inputRequirementsPanel.add(new InlineLabel("Please provide us with a folder that contains the input taxon descriptions and the closest taxon group to be processed. " +
				"You can upload the input taxon descriptions using the "));
		fileManagerAnchor = new Anchor("File Manager");
		inputRequirementsPanel.add(fileManagerAnchor);
		formatRequirementsAnchor = new Anchor("format requirements");
		inputRequirementsPanel.add(new InlineLabel(". See our "));
		inputRequirementsPanel.add(formatRequirementsAnchor);
		inputRequirementsPanel.add(new InlineLabel(" for taxon descriptions."));	
		verticalPanel.add(inputRequirementsPanel);
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.addStyleName("inputForm");
		namePanel.add(new Label("Task name:"));
		nameTextBox = new TextBox();
		namePanel.add(nameTextBox); 
		verticalPanel.add(namePanel); 
		HorizontalPanel taxonDescriptionFilePanel = new HorizontalPanel();
		taxonDescriptionFilePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		taxonDescriptionFilePanel.addStyleName("inputForm");
		taxonDescriptionFileButton = new Button("Select Taxon Descriptions");
		taxonDescriptionFilePanel.add(taxonDescriptionFileButton);
		taxonDescriptionFilePanel.add(taxonDescriptionFileNameLabel);
		verticalPanel.add(taxonDescriptionFilePanel);
		HorizontalPanel glossaryPanel = new HorizontalPanel();
		glossaryPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		glossaryPanel.addStyleName("inputForm");
		glossaryPanel.add(new Label("Taxon group:"));
		glossaryPanel.add(glossaryListBox);
		verticalPanel.add(glossaryPanel);
		verticalPanel.add(nextButton);
		return verticalPanel;
	}

	@Override
	protected TaskStageEnum getStep() {
		return TaskStageEnum.INPUT;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}

	@Override
	public Label getTaxonDescriptionFileNameLabel() {
		return this.taxonDescriptionFileNameLabel;
	}

	@Override
	public Anchor getFormatRequirementsAnchor() {
		return this.formatRequirementsAnchor;
	}

	@Override
	public FocusWidget getTaxonDescriptionFileButton() {
		return this.taxonDescriptionFileButton;
	}

	@Override
	public FocusWidget getFileManagerAnchor() {
		return this.fileManagerAnchor;
	}

	@Override
	public ListBox getGlossaryListBox() {
		return this.glossaryListBox;
	}

	@Override
	public TextBox getNameTextBox() {
		return nameTextBox;
	}

}
