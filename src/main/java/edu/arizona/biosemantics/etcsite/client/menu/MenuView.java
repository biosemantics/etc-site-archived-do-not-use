package edu.arizona.biosemantics.etcsite.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MenuView extends Composite implements IMenuView {

	private static MenuViewUiBinder uiBinder = GWT.create(MenuViewUiBinder.class);

	interface MenuViewUiBinder extends UiBinder<Widget, MenuView> {
	}

	private Presenter presenter;
	
	public MenuView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("semanticMarkupPanel")
	public void onSemanticMarkup(ClickEvent event) {
		presenter.onSemanticMarkup();
	}
	
	@UiHandler("matrixGenerationPanel")
	public void onMatrixGeneration(ClickEvent event) {
		presenter.onMatrixGeneration();
	}
	
	@UiHandler("treeGenerationPanel")
	public void onTreeGeneration(ClickEvent event) {
		presenter.onTreeGeneration();
	}
	
	@UiHandler("taxonomyComparisonPanel")
	public void onTaxonomyComparison(ClickEvent event) {
		presenter.onTaxonomyComparison();
	}
	
	@UiHandler("visualizationPanel")
	public void onVisualization(ClickEvent event) {
		presenter.onVisualization();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	/**
	 * Wouldn't work if it was placed inside MainMenuView (the elements referenced by id wouldn't be
	 * retrievable yet)
	 * http://stackoverflow.com/questions/5613026/gwt-jsni-function-call-to-getelementbyid-returns-null
	 */
	public native void initNativeJavascriptAnimations() /*-{
		var menuHighlightLayer = $doc.getElementById("menuHighlightLayer");
		
		var menuSemanticMarkup = $doc.getElementById("menuSemanticMarkup");
		if(menuSemanticMarkup != null) {
			menuSemanticMarkup.addEventListener("mouseover", menuSemanticMarkupSelect, false);
			menuSemanticMarkup.addEventListener("mouseout", menuDeselect, false);
		}
		
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
		
		function menuSemanticMarkupSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) - 440; 
			var end = (higlightLayerWidth / 2) - 260;
			menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
		}
		
		function menuMatrixGenerationSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) - 260; 
			var end = (higlightLayerWidth / 2) - 80;
			menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
		}
		
		function menuTreeGenerationSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) - 80; 
			var end = (higlightLayerWidth / 2) + 95;
			menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
			
		}
		
		function menuTaxonomyComparisonSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) + 95; 
			var end = (higlightLayerWidth / 2) + 305;
			menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
		}
		
		function menuVisualizationSelect() { 
			var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
			var top = 0; 
			var bottom = menuHighlightLayer.offsetHeight;
			var start = (higlightLayerWidth / 2) + 303; 
			var end = (higlightLayerWidth / 2) + 455;
			menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
		}
		
		function menuDeselect() {
			menuHighlightLayer.style.clip = 'rect(0px, 0px, 0px, 0px)';
		}
	}-*/;
}
