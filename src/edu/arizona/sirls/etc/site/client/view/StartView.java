package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import edu.arizona.sirls.etc.site.client.presenter.StartPresenter;

public class StartView extends Composite implements StartPresenter.Display {

	private final Image matrixGenerationImage = new Image("images/ButtonGreen.gif");
	private Image treeGenerationImage = new Image("images/ButtonGreen.gif");
	private Image taxonomyComparisonImage = new Image("images/ButtonGreen.gif");
	private Image visualizationImage = new Image("images/ButtonGreen.gif");
	private Image pipelineImage = new Image("images/ButtonGray.gif");
	private Label matrixGenerationLabel = new Label("Begin Matrix Generation");;
	private Label treeGenerationLabel = new Label("Begin Tree Generation");
	private Label taxonomyComparisonLabel = new Label("Begin Taxonomy Comparison");
	private Label visualizationLabel = new Label("Begin Visualization");
	private Label pipelineLabel = new Label("Begin New Pipeline");

	public StartView() { 
		matrixGenerationImage.setWidth("200px");
		matrixGenerationImage.setHeight("50px");
		treeGenerationImage.setWidth("200px");
		treeGenerationImage.setHeight("50px");
		taxonomyComparisonImage.setWidth("200px");
		taxonomyComparisonImage.setHeight("50px");
		visualizationImage.setWidth("200px");
		visualizationImage.setHeight("50px");
		pipelineImage.setWidth("200px");
		pipelineImage.setHeight("50px");
		
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='matrixGenerationImage' class='actionImage'></div>" +
				"<div id='matrixGenerationText' class='actionText'></div>" +
				"<div id='matrixGenerationDescription' class='actionDescription'>" +
						"Matrix generation generates taxon-character matrices from clean textual descriptions of organisms.	The matrices can be used to create " +
						"specimen identification keys or be used as input to the Tree Generation tool. </div>" +
				"<div id='matrixGenerationArrow' class='actionArrow'></div>" +
				"<div id='matrixGenerationButton' class='actionButton clickable'></div>" +
				"<div id='matrixGenerationButtonText' class='actionButtonText clickable'></div>" +
				"<div id='treeGenerationImage' class='actionImage'></div>" +
				"<div id='treeGenerationText' class='actionText'></div>" +
				"<div id='treeGenerationDescription' class='actionDescription'>" +
				"Tree Generation generates candidate taxonomies from taxon-character matrices, based on the similarity of characters of taxa. The character-similarity based " +
				"taxonomies may be one source of input for the Taxonomy Comparison tool.</div>" +
				"<div id='treeGenerationArrow' class='actionArrow'></div>" +
				"<div id='treeGenerationButton' class='actionButton clickable'></div>" +
				"<div id='treeGenerationButtonText' class='actionButtonText clickable'></div>" +
				"<div id='taxonomyComparisonImage' class='actionImage'></div>" +
				"<div id='taxonomyComparisonText' class='actionText'></div>" +
				"<div id='taxonomyComparisonDescription' class='actionDescription'>" +
						"Taxonomy Comparison takes expert provided relationships among taxa and optionally the result from the Tree Generation to perform " +
						"logic reasoning and identify logic conflicts and/or ambiguities.</div>" +
				"<div id='taxonomyComparisonArrow' class='actionArrow'></div>" +
				"<div id='taxonomyComparisonButton' class='actionButton clickable'></div>" +
				"<div id='taxonomyComparisonButtonText' class='actionButtonText clickable'></div>" +
				"<div id='visualizationImage' class='actionImage'></div>" +
				"<div id='visualizationText' class='actionText'></div>" +
				"<div id='visualizationDescription' class='actionDescription'>Visualization integrates " +
						"relationships among taxa and relationships between character and taxa in a visual way to facilitate taxon concept comparison and analysis. </div>" +
				"<div id='visualizationArrow' class='actionArrow'></div>" +
				"<div id='visualizationButton' class='actionButton clickable'></div>" +
				"<div id='visualizationButtonText' class='actionButtonText clickable'></div>" +
				"<div id='orText'></div>" +
				"<div id='pipelineButton'></div>" +
				"<div id='pipelineButtonText'></div>" +
				"</div>" +
				"</div>");
		htmlPanel.add(matrixGenerationImage, "matrixGenerationButton");
		htmlPanel.add(this.matrixGenerationLabel, "matrixGenerationButtonText");
		htmlPanel.add(this.treeGenerationImage, "treeGenerationButton");
		htmlPanel.add(this.treeGenerationLabel, "treeGenerationButtonText");
		htmlPanel.add(this.taxonomyComparisonImage, "taxonomyComparisonButton");
		htmlPanel.add(this.taxonomyComparisonLabel, "taxonomyComparisonButtonText");
		htmlPanel.add(this.visualizationImage, "visualizationButton");
		htmlPanel.add(this.visualizationLabel, "visualizationButtonText");
		htmlPanel.add(this.pipelineImage, "pipelineButton");
		htmlPanel.add(this.pipelineLabel, "pipelineButtonText");
		
		initWidget(htmlPanel);
	}
	
	@Override
	public HasClickHandlers getMatrixGenerationButton() {
		return matrixGenerationImage;
	}

	@Override
	public HasClickHandlers getPipelineButton() {
		return this.pipelineImage;
	}

	@Override
	public HasClickHandlers getVisualizationButton() {
		return this.visualizationImage;
	}

	@Override
	public HasClickHandlers getTaxonomyComparisonButton() {
		return this.taxonomyComparisonImage;
	}

	@Override
	public HasClickHandlers getTreeGenerationButton() {
		return this.treeGenerationImage;
	}

	@Override
	public HasClickHandlers getMatrixGenerationLabel() {
		return this.matrixGenerationLabel;
	}
	
	@Override
	public HasClickHandlers getTreeGenerationLabel() {
		return this.treeGenerationLabel;
	}
	
	@Override
	public HasClickHandlers getTaxonomyComparisonLabel() {
		return this.taxonomyComparisonLabel;
	}
	
	@Override
	public HasClickHandlers getVisualizationLabel() {
		return this.visualizationLabel;
	}

	@Override
	public HasClickHandlers getPipelineLabel() {
		return this.pipelineLabel;
	}
}
