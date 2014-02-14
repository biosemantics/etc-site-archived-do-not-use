package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView.ILoginListener;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView.Presenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.pipeline.PipelinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.menu.IStartMenuView;
import edu.arizona.biosemantics.etcsite.client.top.LoggedInPlace;

public class HomeActivity extends MyAbstractActivity implements IStartMenuView.Presenter, IHomeContentView.Presenter {

    private PlaceController placeController;
	private IHomeContentView homeContentView;
	private Presenter loginPresenter;

	@Inject
	public HomeActivity(IHomeContentView homeContentView, PlaceController placeController, ILoginView.Presenter loginPresenter) {
		this.homeContentView = homeContentView;
    	this.placeController = placeController;
    	this.loginPresenter = loginPresenter;
    }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		homeContentView.setPresenter(this);
		panel.setWidget(homeContentView.asWidget());
	}

	@Override
	public void onMatrixGeneration() {
		checkLogin(new MatrixGenerationInputPlace());
	}

	@Override
	public void onSemanticMarkup() {
		checkLogin(new SemanticMarkupInputPlace());
	}

	@Override
	public void onTaxonomyComparison() {
		checkLogin(new TaxonomyComparisonPlace());
	}

	@Override
	public void onVisualization() {
		checkLogin(new VisualizationPlace());
	}

	@Override
	public void onPipeline() {
		checkLogin(new PipelinePlace());
	}

	@Override
	public void onTreeGeneration() {
		checkLogin(new TreeGenerationPlace());
	}
	
	private void checkLogin(final Place gotoPlace) {
		if(Authentication.getInstance().isSet()) {
			placeController.goTo(new LoggedInPlace());
			placeController.goTo(gotoPlace);
		} else {
			loginPresenter.show(new ILoginListener() {
				@Override
				public void onLogin() {
					placeController.goTo(new LoggedInPlace());
					placeController.goTo(gotoPlace);
				}
				@Override
				public void onLoginFailure() {
				}
			});
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
