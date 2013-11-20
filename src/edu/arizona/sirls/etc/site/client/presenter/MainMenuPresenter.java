package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.event.TaxonomyComparisonEvent;
import edu.arizona.sirls.etc.site.client.event.TreeGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.VisualizationEvent;

public class MainMenuPresenter implements Presenter {

	private HandlerManager eventBus;
	public interface Display {
		Widget asWidget();
		Widget getSemanticMarkupLinkPanel();
		Widget getTreeGenerationLinkPanel();
		Widget getTaxonomyComparisonLinkPanel();
		Widget getVisualizationLinkPanel();
	}
	private final Display display;

	public MainMenuPresenter(HandlerManager eventBus, Display display) {
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		display.getSemanticMarkupLinkPanel().sinkEvents(Event.ONCLICK);	
		display.getSemanticMarkupLinkPanel().addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SemanticMarkupEvent());
			}
		}, ClickEvent.getType());
		
		display.getTreeGenerationLinkPanel().sinkEvents(Event.ONCLICK);	
		display.getTreeGenerationLinkPanel().addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TreeGenerationEvent());
			}
		}, ClickEvent.getType());
		
		display.getTaxonomyComparisonLinkPanel().sinkEvents(Event.ONCLICK);	
		display.getTaxonomyComparisonLinkPanel().addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TaxonomyComparisonEvent());
			}
		}, ClickEvent.getType());
		
		display.getVisualizationLinkPanel().sinkEvents(Event.ONCLICK);	
		display.getVisualizationLinkPanel().addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new VisualizationEvent());
			}
		}, ClickEvent.getType());
	}

	@Override
	public void go(HasWidgets content) {
		content.clear();
	    content.add(display.asWidget());
	    initNativeJavascriptAnimations();
	}
	
	/**
	 * Wouldn't work if it was placed inside MainMenuView (the elements referenced by id wouldn't be
	 * retrievable yet)
	 */
	public static native void initNativeJavascriptAnimations() /*-{
		var menuHighlightLayer = $doc.getElementById("menuHighlightLayer");
		
		var menuSemanticMarkup = $doc.getElementById("menuSemanticMarkup");
		if(menuSemanticMarkup != null) {
			menuSemanticMarkup.addEventListener("mouseover", menuSemanticMarkupSelect, false);
			menuSemanticMarkup.addEventListener("mouseout", menuDeselect, false);
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
