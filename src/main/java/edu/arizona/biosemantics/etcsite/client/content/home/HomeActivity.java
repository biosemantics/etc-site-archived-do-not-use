package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.pipeline.PipelinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.menu.IStartMenuView;

public class HomeActivity extends MyAbstractActivity implements IStartMenuView.Presenter, IHomeContentView.Presenter {

    private PlaceController placeController;
	private IHomeContentView homeContentView;

	@Inject
	public HomeActivity(IHomeContentView homeContentView, PlaceController placeController) {
		this.homeContentView = homeContentView;
    	this.placeController = placeController;
    }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		homeContentView.setPresenter(this);
		panel.setWidget(homeContentView.asWidget());
	}

	@Override
	public void onMatrixGeneration() {
		placeController.goTo(new MatrixGenerationInputPlace());
	}

	@Override
	public void onSemanticMarkup() {
		placeController.goTo(new SemanticMarkupInputPlace());
	}

	@Override
	public void onTaxonomyComparison() {
		placeController.goTo(new TaxonomyComparisonPlace());
	}

	@Override
	public void onVisualization() {
		placeController.goTo(new VisualizationPlace());
	}

	@Override
	public void onPipeline() {
		placeController.goTo(new PipelinePlace());
	}

	@Override
	public void onTreeGeneration() {
		placeController.goTo(new TreeGenerationPlace());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
