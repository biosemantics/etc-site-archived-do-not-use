package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.InputMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.shared.rpc.Step;

public class InputMatrixGenerationView extends MatrixGenerationView implements 
		InputMatrixGenerationPresenter.Display {

	private Label taxonGlossaryFileNameLabel;
	private Label taxonDescriptionFileNameLabel;
	private Button nextButton;
	private FocusWidget taxonDescriptionFileButton;
	private Anchor formatRequirementsAnchor;
	private Button taxonGlossaryFileButton;
	private FocusWidget fileManagerAnchor;

	@Override
	protected Widget getStepWidget() {
		this.taxonGlossaryFileNameLabel = new Label();
		this.taxonDescriptionFileNameLabel = new Label();
		this.nextButton = new Button("Next");
		this.taxonDescriptionFileButton = new Button("Select");
		this.formatRequirementsAnchor = new Anchor("Format Requirements");
		this.taxonGlossaryFileButton = new Button("Select");
		this.fileManagerAnchor = new Anchor("File Manager");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("contentPanel");
		verticalPanel.add(new Label("Input"));
		FlowPanel inputRequirementsPanel = new FlowPanel(); 
		inputRequirementsPanel.add(new InlineLabel("Input taxon description text and glossary. ")); 
		formatRequirementsAnchor = new Anchor("See Format Requirements");
		inputRequirementsPanel.add(formatRequirementsAnchor);
		verticalPanel.add(inputRequirementsPanel);
		HorizontalPanel taxonDescriptionFilePanel = new HorizontalPanel();
		taxonDescriptionFileButton = new Button("Select Taxon Description File");
		taxonDescriptionFilePanel.add(taxonDescriptionFileButton);
		taxonDescriptionFilePanel.add(taxonDescriptionFileNameLabel);
		verticalPanel.add(taxonDescriptionFilePanel);
		HorizontalPanel taxonGlossaryFilePanel = new HorizontalPanel();
		taxonGlossaryFileButton = new Button("Select Taxon Glossary File");
		taxonGlossaryFilePanel.add(taxonGlossaryFileButton);
		taxonGlossaryFilePanel.add(taxonGlossaryFileNameLabel);
		verticalPanel.add(taxonGlossaryFilePanel);
		FlowPanel fileManagerPanel = new FlowPanel();
		fileManagerPanel.add(new InlineLabel("Or upload in "));
		fileManagerAnchor = new Anchor("File Manager");
		fileManagerPanel.add(fileManagerAnchor);
		verticalPanel.add(fileManagerPanel);
		verticalPanel.add(nextButton);
		return verticalPanel;
	}

	@Override
	protected Step getStep() {
		return Step.INPUT;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}

	@Override
	public Label getTaxonGlossaryFileNameLabel() {
		return this.taxonGlossaryFileNameLabel;
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
	public FocusWidget getTaxonGlossaryFileButton() {
		return this.taxonGlossaryFileButton;
	}

	@Override
	public FocusWidget getFileManagerAnchor() {
		return this.fileManagerAnchor;
	}

}
