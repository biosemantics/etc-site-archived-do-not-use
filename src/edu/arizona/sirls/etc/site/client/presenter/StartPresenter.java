package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.PipelineEvent;
import edu.arizona.sirls.etc.site.client.event.TaxonomyComparisonEvent;
import edu.arizona.sirls.etc.site.client.event.TreeGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.VisualizationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.MatrixGenerationEvent;

public class StartPresenter implements Presenter {

	private HandlerManager eventBus;
	public interface Display {
		HasClickHandlers getMatrixGenerationButton();
		Widget asWidget();
		HasClickHandlers getPipelineButton();
		HasClickHandlers getVisualizationButton();
		HasClickHandlers getTaxonomyComparisonButton();
		HasClickHandlers getTreeGenerationButton();
		HasClickHandlers getMatrixGenerationLabel();
		HasClickHandlers getVisualizationLabel();
		HasClickHandlers getTaxonomyComparisonLabel();
		HasClickHandlers getTreeGenerationLabel();
		HasClickHandlers getPipelineLabel();
	}
	private final Display display;

	public StartPresenter(HandlerManager eventBus, Display display) {
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	@Override
	public void go(HasWidgets content) {
		content.clear();
	    content.add(display.asWidget());
	}
  
	public void bind() {
		ClickHandler matrixGenerationClickHandler = new ClickHandler() { 
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new MatrixGenerationEvent());
			}
		};
		ClickHandler treeGenerationClickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TreeGenerationEvent());
			}
		};
		ClickHandler taxonomyComparisonClickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TaxonomyComparisonEvent());
			}
		};
		ClickHandler visualizationClickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new VisualizationEvent());
			}
		};
		ClickHandler pipelineClickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new PipelineEvent());
			}
		};
		
		this.display.getMatrixGenerationLabel().addClickHandler(matrixGenerationClickHandler);
		this.display.getMatrixGenerationButton().addClickHandler(matrixGenerationClickHandler);
		this.display.getTreeGenerationLabel().addClickHandler(treeGenerationClickHandler);
		this.display.getTreeGenerationButton().addClickHandler(treeGenerationClickHandler);
		this.display.getTaxonomyComparisonLabel().addClickHandler(taxonomyComparisonClickHandler);
		this.display.getTaxonomyComparisonButton().addClickHandler(taxonomyComparisonClickHandler);
		this.display.getVisualizationLabel().addClickHandler(visualizationClickHandler);
		this.display.getVisualizationButton().addClickHandler(visualizationClickHandler);
		this.display.getPipelineLabel().addClickHandler(pipelineClickHandler);
		this.display.getPipelineButton().addClickHandler(pipelineClickHandler);
	}
}
