package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.MainMenuPresenter;

public class MainMenuView extends Composite implements MainMenuPresenter.Display {

	private Widget semanticMarkupLinkPanel;
	private Widget treeGenerationLinkPanel;
	private Widget taxonomyComparisonLinkPanel;
	private Widget visualizationLinkPanel;
	private Widget matrixGenerationLinkPanel;

	public MainMenuView() {
		HTMLPanel htmlPanel = new HTMLPanel(
				"<div id='menuBackgroundLayer'></div><div id='menuHighlightLayer'></div>" +
				"<div id='menuLayer'>" +
				"<div id='menuSemanticMarkup' class='menuAction clickable'>" +
				"<div id='menuSemanticMarkupIcon' class='menuActionIcon'></div>" +
				"<div id='menuSemanticMarkupText' class='menuActionText'>Semantic Markup</div>" +
				"<div id='menuSemanticMarkupLink' class='menuAction'></div>" +
				"</div>" +	
				"<div id='menuMatrixGeneration' class='menuAction clickable'>" +
				"<div id='menuMatrixGenerationIcon' class='menuActionIcon'></div>" +
				"<div id='menuMatrixGenerationText' class='menuActionText'>Matrix Generation</div>" +
				"<div id='menuMatrixGenerationLink' class='menuAction'></div>" +
				"</div>" +
				"<div id='menuTreeGeneration' class='menuAction clickable'>" +
				"<div id='menuTreeGenerationIcon' class='menuActionIcon'></div>" +
				"<div id='menuTreeGenerationText' class='menuActionText'>Tree Generation</div>" +
				"<div id='menuTreeGenerationLink' class='menuAction'></div>" +
				"</div>" +
				"<div id='menuTaxonomyComparison' class='menuAction clickable'>" +
				"<div id='menuTaxonomyComparisonIcon' class='menuActionIcon'></div>" +
				"<div id='menuTaxonomyComparisonText' class='menuActionText'>Taxonomy Comparison</div>" +
				"<div id='menuTaxonomyComparisonLink' class='menuAction'></div>" +
				"</div>" +
				"<div id='menuVisualization' class='menuAction clickable'>" +
				"<div id='menuVisualizationIcon' class='menuActionIcon'></div>" +
				"<div id='menuVisualizationText' class='menuActionText'>Visualization</div>" +
				"<div id='menuVisualizationLink' class='menuAction'></div>" +
				"</div>" +
				"</div>");
		
		semanticMarkupLinkPanel = new SimplePanel();
		semanticMarkupLinkPanel.setWidth("200px");
		semanticMarkupLinkPanel.setHeight("130px");
		htmlPanel.add(semanticMarkupLinkPanel, "menuSemanticMarkupLink");
		
		matrixGenerationLinkPanel = new SimplePanel();
		matrixGenerationLinkPanel.setWidth("200px");
		matrixGenerationLinkPanel.setHeight("130px");
		htmlPanel.add(matrixGenerationLinkPanel, "menuMatrixGenerationLink");
		
		treeGenerationLinkPanel = new SimplePanel();
		treeGenerationLinkPanel.setWidth("200px");
		treeGenerationLinkPanel.setHeight("130px");
		htmlPanel.add(treeGenerationLinkPanel, "menuTreeGenerationLink");
		
		taxonomyComparisonLinkPanel = new SimplePanel();
		taxonomyComparisonLinkPanel.setWidth("240px");
		taxonomyComparisonLinkPanel.setHeight("130px");
		htmlPanel.add(taxonomyComparisonLinkPanel, "menuTaxonomyComparisonLink");
		
		visualizationLinkPanel = new SimplePanel();
		visualizationLinkPanel.setWidth("180px");
		visualizationLinkPanel.setHeight("130px");
		htmlPanel.add(visualizationLinkPanel, "menuVisualizationLink");
		
		this.initWidget(htmlPanel);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public Widget getSemanticMarkupLinkPanel() {
		return this.semanticMarkupLinkPanel;
	}

	@Override
	public Widget getTreeGenerationLinkPanel() {
		return this.treeGenerationLinkPanel;
	}

	@Override
	public Widget getTaxonomyComparisonLinkPanel() {
		return this.taxonomyComparisonLinkPanel;
	}

	@Override
	public Widget getVisualizationLinkPanel() {
		return this.visualizationLinkPanel;
	}

}
