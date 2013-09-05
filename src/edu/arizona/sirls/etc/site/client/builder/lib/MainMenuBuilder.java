package edu.arizona.sirls.etc.site.client.builder.lib;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.arizona.sirls.etc.site.client.builder.IMenuBuilder;
import edu.arizona.sirls.etc.site.client.builder.handler.MatrixGenerationClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.TaxonomyComparisonClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.TreeGenerationClickHandler;
import edu.arizona.sirls.etc.site.client.builder.handler.VisualizationClickHandler;

public class MainMenuBuilder implements IMenuBuilder { 
	
	@Override
	public void build() {
		createHTML();
		initWidgets();
		initNativeJavascriptAnimations();
		
	}

	private void initWidgets() {
		SimplePanel matrixGenerationLinkPanel = new SimplePanel();
		matrixGenerationLinkPanel.sinkEvents(Event.ONCLICK);		
		matrixGenerationLinkPanel.setWidth("200px");
		matrixGenerationLinkPanel.setHeight("130px");
		matrixGenerationLinkPanel.addHandler(new MatrixGenerationClickHandler(), ClickEvent.getType());
		RootPanel.get("menuMatrixGenerationLink").add(matrixGenerationLinkPanel);
		
		SimplePanel treeGenerationLinkPanel = new SimplePanel();
		treeGenerationLinkPanel.sinkEvents(Event.ONCLICK);	
		treeGenerationLinkPanel.setWidth("200px");
		treeGenerationLinkPanel.setHeight("130px");
		treeGenerationLinkPanel.addHandler(new TreeGenerationClickHandler(), ClickEvent.getType());
		RootPanel.get("menuTreeGenerationLink").add(treeGenerationLinkPanel);
		
		SimplePanel taxonomyComparisonLinkPanel = new SimplePanel();
		taxonomyComparisonLinkPanel.sinkEvents(Event.ONCLICK);	
		taxonomyComparisonLinkPanel.setWidth("240px");
		taxonomyComparisonLinkPanel.setHeight("130px");
		taxonomyComparisonLinkPanel.addHandler(new TaxonomyComparisonClickHandler(), ClickEvent.getType());
		RootPanel.get("menuTaxonomyComparisonLink").add(taxonomyComparisonLinkPanel);
		
		SimplePanel visualizationLinkPanel = new SimplePanel();
		visualizationLinkPanel.sinkEvents(Event.ONCLICK);	
		visualizationLinkPanel.setWidth("180px");
		visualizationLinkPanel.setHeight("130px");
		visualizationLinkPanel.addHandler(new VisualizationClickHandler(), ClickEvent.getType());
		RootPanel.get("menuVisualizationLink").add(visualizationLinkPanel);
	}

	private void createHTML() {
		Element menu = DOM.getElementById("menu"); // RootPanel.get("menu");
		//HTMLPanel menuHTMLPanel = new HTMLPanel(
		menu.setInnerHTML(
				"<div id='menuBackgroundLayer'></div><div id='menuHighlightLayer'></div>" +
				"<div id='menuLayer'>" +
				"<div id='menuMatrixGeneration' class='menuAction'>" +
				"<div id='menuMatrixGenerationIcon' class='menuActionIcon'></div>" +
				"<div id='menuMatrixGenerationText' class='menuActionText'>Matrix Generation</div>" +
				"<div id='menuMatrixGenerationLink' class='menuAction'></div>" +
				"</div>" +
				"<div id='menuTreeGeneration' class='menuAction'>" +
				"<div id='menuTreeGenerationIcon' class='menuActionIcon'></div>" +
				"<div id='menuTreeGenerationText' class='menuActionText'>Tree Generation</div>" +
				"<div id='menuTreeGenerationLink' class='menuAction'></div>" +
				"</div>" +
				"<div id='menuTaxonomyComparison' class='menuAction'>" +
				"<div id='menuTaxonomyComparisonIcon' class='menuActionIcon'></div>" +
				"<div id='menuTaxonomyComparisonText' class='menuActionText'>Taxonomy Comparison</div>" +
				"<div id='menuTaxonomyComparisonLink' class='menuAction'></div>" +
				"</div>" +
				"<div id='menuVisualization' class='menuAction'>" +
				"<div id='menuVisualizationIcon' class='menuActionIcon'></div>" +
				"<div id='menuVisualizationText' class='menuActionText'>Visualization</div>" +
				"<div id='menuVisualizationLink' class='menuAction'></div>" +
				"</div>" +
				"</div>");
		//menuPanel.add(menuHTMLPanel);
		initNativeJavascriptAnimations();
	}
	
	public static native void initNativeJavascriptAnimations() /*-{
		var menuHighlightLayer = $doc.getElementById("menuHighlightLayer");
		
		var menuMatrixGeneration = $doc.getElementById("menuMatrixGeneration");
		if(menuMatrixGeneration != null) {
			menuMatrixGeneration.addEventListener("mouseover", menuMatrixGenerationSelect, false);
			menuMatrixGeneration.addEventListener("mouseout", menuDeselect, false);
		}
		
		var menuTreeGeneration = $doc.getElementById("menuTreeGeneration");
		if(menuTreeGeneration != null) {
			menuTreeGeneration.addEventListener("mouseover", menuTreeGenerationSelect, false);
			menuTreeGeneration.addEventListener("mouseout", menuDeselect, false);
		}
		
		var menuTaxonomyComparison = $doc.getElementById("menuTaxonomyComparison");
		if(menuTaxonomyComparison != null) {
			menuTaxonomyComparison.addEventListener("mouseover", menuTaxonomyComparisonSelect, false);
			menuTaxonomyComparison.addEventListener("mouseout", menuDeselect, false);
		}
		
		var menuVisualization = $doc.getElementById("menuVisualization");
		if(menuVisualization != null) { 
			menuVisualization.addEventListener("mouseover", menuVisualizationSelect, false);
			menuVisualization.addEventListener("mouseout", menuDeselect, false);
		}
		
		function menuMatrixGenerationSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) - 400; 
			var end = (higlightLayerWidth / 2) - 400 + 200;
			menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
		}
		
		function menuTreeGenerationSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) - 200; 
			var end = (higlightLayerWidth / 2) - 200 + 200;
			menuHighlightLayer.style.clip = 'rect(' + top + ', ' + end + ', ' + bottom + ', ' + start + ')';
		}
		
		function menuTaxonomyComparisonSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) + 200 - 200; 
			var end = (higlightLayerWidth / 2) + 240;
			menuHighlightLayer.style.clip = 'rect(' + top + ', ' + end + ', ' + bottom + ', ' + start + ')';
		}
		
		function menuVisualizationSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) + 440 - 200; 
			var end = (higlightLayerWidth / 2) + 420;
			menuHighlightLayer.style.clip = 'rect(' + top + ', ' + end + ', ' + bottom + ', ' + start + ')';
		}
		
		function menuDeselect() {  
			menuHighlightLayer.style.clip = 'rect(0, 0, 0, 0)';
		}
	}-*/;
	
}
