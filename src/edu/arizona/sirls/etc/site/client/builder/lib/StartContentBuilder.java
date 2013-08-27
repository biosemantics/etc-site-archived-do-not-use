package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.handler.MatrixGenerationClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.TaxonomyComparisonClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.TreeGenerationClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.VisualizationClickHandler;

public class StartContentBuilder implements IContentBuilder {
	
	@Override
	public void build() {
		createHTML();
		initWidgets();
	}

	private void initWidgets() {
		Image matrixGenerationImage = new Image("images/ButtonGreen.gif");
		matrixGenerationImage.setWidth("200px");
		matrixGenerationImage.setHeight("50px");
		RootPanel.get("matrixGenerationButton").add(matrixGenerationImage);
		Label matrixGenerationLabel = new Label("Begin Matrix Generation");
		RootPanel.get("matrixGenerationButtonText").add(matrixGenerationLabel);
		
		Image treeGenerationImage = new Image("images/ButtonGreen.gif");
		treeGenerationImage.setWidth("200px");
		treeGenerationImage.setHeight("50px");
		RootPanel.get("treeGenerationButton").add(treeGenerationImage);
		Label treeGenerationLabel = new Label("Begin Tree Generation");
		RootPanel.get("treeGenerationButtonText").add(treeGenerationLabel);
		
		Image taxonomyComparisonImage = new Image("images/ButtonGreen.gif");
		taxonomyComparisonImage.setWidth("200px");
		taxonomyComparisonImage.setHeight("50px");
		RootPanel.get("taxonomyComparisonButton").add(taxonomyComparisonImage);
		Label taxonomyComparisonLabel = new Label("Begin Taxonomy Comparison");
		RootPanel.get("taxonomyComparisonButtonText").add(taxonomyComparisonLabel);
		
		Image visualizationImage = new Image("images/ButtonGreen.gif");
		visualizationImage.setWidth("200px");
		visualizationImage.setHeight("50px");
		RootPanel.get("visualizationButton").add(visualizationImage);
		Label visualizationLabel = new Label("Begin Visualization");
		RootPanel.get("visualizationButtonText").add(visualizationLabel);
		
		Image pipelineImage = new Image("images/ButtonGray.gif");
		pipelineImage.setWidth("200px");
		pipelineImage.setHeight("50px");
		RootPanel.get("pipelineButton").add(pipelineImage);
		Label pipelineLabel = new Label("Begin New Pipeline");
		RootPanel.get("pipelineButtonText").add(pipelineLabel);

		MatrixGenerationClickHandler matrixGenerationClickHandler = new MatrixGenerationClickHandler();
		matrixGenerationImage.addClickHandler(matrixGenerationClickHandler);
		matrixGenerationLabel.addClickHandler(matrixGenerationClickHandler);
		
		TreeGenerationClickHandler treeGenerationClickHandler = new TreeGenerationClickHandler();
		treeGenerationImage.addClickHandler(treeGenerationClickHandler);
		treeGenerationLabel.addClickHandler(treeGenerationClickHandler);
		
		TaxonomyComparisonClickHandler taxonomyComparisonClickHandler = new TaxonomyComparisonClickHandler();
		taxonomyComparisonImage.addClickHandler(taxonomyComparisonClickHandler);
		taxonomyComparisonLabel.addClickHandler(taxonomyComparisonClickHandler);
		
		VisualizationClickHandler visualizationClickHandler = new VisualizationClickHandler();
		visualizationImage.addClickHandler(visualizationClickHandler);
		visualizationLabel.addClickHandler(visualizationClickHandler);		
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML(
				"<div class='content900pxCentered'>" +
				"<div id='matrixGenerationImage' class='actionImage'></div>" +
				"<div id='matrixGenerationText' class='actionText'></div>" +
				"<div id='matrixGenerationDescription' class='actionDescription'>" +
						"Matrix generation generates taxon-character matrices from clean textual descriptions of organisms.	The matrices can be used to create " +
						"specimen identification keys or be used as input to the Tree Generation tool. </div>" +
				"<div id='matrixGenerationArrow' class='actionArrow'></div>" +
				"<div id='matrixGenerationButton' class='actionButton'></div>" +
				"<div id='matrixGenerationButtonText' class='actionButtonText'></div>" +
				"<div id='treeGenerationImage' class='actionImage'></div>" +
				"<div id='treeGenerationText' class='actionText'></div>" +
				"<div id='treeGenerationDescription' class='actionDescription'>" +
				"Tree Generation generates candidate taxonomies from taxon-character matrices, based on the similarity of characters of taxa. The character-similarity based " +
				"taxonomies may be one source of input for the Taxonomy Comparison tool.</div>" +
				"<div id='treeGenerationArrow' class='actionArrow'></div>" +
				"<div id='treeGenerationButton' class='actionButton'></div>" +
				"<div id='treeGenerationButtonText' class='actionButtonText'></div>" +
				"<div id='taxonomyComparisonImage' class='actionImage'></div>" +
				"<div id='taxonomyComparisonText' class='actionText'></div>" +
				"<div id='taxonomyComparisonDescription' class='actionDescription'>" +
						"Taxonomy Comparison takes expert provided relationships among taxa and optionally the result from the Tree Generation to perform " +
						"logic reasoning and identify logic conflicts and/or ambiguities.</div>" +
				"<div id='taxonomyComparisonArrow' class='actionArrow'></div>" +
				"<div id='taxonomyComparisonButton' class='actionButton'></div>" +
				"<div id='taxonomyComparisonButtonText' class='actionButtonText'></div>" +
				"<div id='visualizationImage' class='actionImage'></div>" +
				"<div id='visualizationText' class='actionText'></div>" +
				"<div id='visualizationDescription' class='actionDescription'>Visualization integrates " +
						"relationships among taxa and relationships between character and taxa in a visual way to facilitate taxon concept comparison and analysis. </div>" +
				"<div id='visualizationArrow' class='actionArrow'></div>" +
				"<div id='visualizationButton' class='actionButton'></div>" +
				"<div id='visualizationButtonText' class='actionButtonText'></div>" +
				"<div id='orText'></div>" +
				"<div id='pipelineButton'></div>" +
				"<div id='pipelineButtonText'></div>" +
				"</div>" +
				"</div>");
	}

}
