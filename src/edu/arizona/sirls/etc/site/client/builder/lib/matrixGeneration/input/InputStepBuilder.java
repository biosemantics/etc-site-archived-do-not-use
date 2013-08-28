package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.dialog.CloseDialogBoxClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.FileManagerClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.FileManagerPopupClickHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.preprocess.PreprocessStepBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.FileFilter;

public class InputStepBuilder implements IStepBuilder, IFileSelectClickHandlerListener {
	
	private TaxonDescriptionFileSelectClickHandler selectTaxonDescriptionClickHandler = new TaxonDescriptionFileSelectClickHandler();
	private GlossaryFileSelectClickHandler selectGlossaryFileClickHandler = new GlossaryFileSelectClickHandler();
	private Label taxonGlossaryFileNameLabel = new Label();
	private Label taxonDescriptionFileNameLabel = new Label();
	private Button nextButton = new Button("Next");
	private MatrixGenerationJob matrixGenerationJob;
	
	public InputStepBuilder(MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
	}
	
	@Override
	public void build(Panel panel) {
		panel.add(new Label("Input"));
		
		HorizontalPanel inputRequirementsPanel = new HorizontalPanel();
		inputRequirementsPanel.add(new Label("Input taxon description text and glossary. "));
		
		Anchor formatRequirementsAnchor = new Anchor("Format Requirements");
		formatRequirementsAnchor.addClickHandler(new FormatRequirementsClickHandler());
		
		inputRequirementsPanel.add(formatRequirementsAnchor);
		panel.add(inputRequirementsPanel);
		
		HorizontalPanel taxonDescriptionFilePanel = new HorizontalPanel();
		Button taxonDescriptionFileButton = new Button("Select Taxon Description File");
		
		selectTaxonDescriptionClickHandler.addListener(this);
		CloseDialogBoxClickHandler closeClickHandler = new CloseDialogBoxClickHandler();
		
		FileSelectDialogClickHandler fileSelectClickHandler = new FileSelectDialogClickHandler(FileFilter.TAXON_DESCRIPTION, 
				closeClickHandler, selectTaxonDescriptionClickHandler);
		taxonDescriptionFileButton.addClickHandler(fileSelectClickHandler);
		taxonDescriptionFilePanel.add(taxonDescriptionFileButton);
		taxonDescriptionFilePanel.add(taxonDescriptionFileNameLabel);
		panel.add(taxonDescriptionFilePanel);
		
		HorizontalPanel taxonGlossaryFilePanel = new HorizontalPanel();
		Button taxonGlossaryFileButton = new Button("Select Taxon Glossary File");
		
		selectGlossaryFileClickHandler.addListener(this);
		fileSelectClickHandler = new FileSelectDialogClickHandler(FileFilter.GLOSSARY_FILE, 
				closeClickHandler, selectGlossaryFileClickHandler);
		taxonGlossaryFileButton.addClickHandler(fileSelectClickHandler);
		taxonGlossaryFilePanel.add(taxonGlossaryFileButton);
		taxonGlossaryFilePanel.add(taxonGlossaryFileNameLabel);
		panel.add(taxonGlossaryFilePanel);
		
		HorizontalPanel fileManagerPanel = new HorizontalPanel();
		fileManagerPanel.add(new Label("Or upload in "));
		
		Anchor fileManagerAnchor = new Anchor("File Manager");
		fileManagerAnchor.addClickHandler(new FileManagerPopupClickHandler());
		fileManagerPanel.add(fileManagerAnchor);
		panel.add(fileManagerPanel);
		
		if(!matrixGenerationJob.hasTaxonDescriptionFile())
			nextButton.setEnabled(false);
		
		nextButton.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				if(matrixGenerationJob.hasTaxonDescriptionFile()) {
					PageBuilder pageBuilder = Session.getInstance().getPageBuilder();
					pageBuilder.setContentBuilder(new MatrixGenerationContentBuilder(new PreprocessStepBuilder(matrixGenerationJob)));
					pageBuilder.build();
				}
			}
		});
		panel.add(nextButton);
	}

	@Override
	public Step getStep() {
		return Step.INPUT;
	}

	@Override
	public void notifyFileSelect(FileSelectClickHandler fileSelectClickHandler,	String selection) {		
		if(fileSelectClickHandler.equals(selectTaxonDescriptionClickHandler)) {
			this.taxonDescriptionFileNameLabel.setText(selection);
			matrixGenerationJob.setTaxonDescriptionFile(selection);
			nextButton.setEnabled(true);
		}
		if(fileSelectClickHandler.equals(selectGlossaryFileClickHandler)) {
			this.taxonGlossaryFileNameLabel.setText(selection);
			matrixGenerationJob.setTaxonGlossaryFile(selection);
		}
	}

}
