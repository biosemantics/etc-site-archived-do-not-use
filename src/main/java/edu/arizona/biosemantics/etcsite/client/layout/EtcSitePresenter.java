package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;

public class EtcSitePresenter implements IEtcSiteView.Presenter {

	private IEtcSiteView view;
	private MyActivityManager contentActivityManager;
	private PlaceController placeController;

	@Inject
	public EtcSitePresenter(IEtcSiteView view, @Named("Content") MyActivityManager contentActivityManager, 
			PlaceController placeController) {
		this.view = view;
		view.setPresenter(this);
		this.contentActivityManager = contentActivityManager;
		this.placeController = placeController;
	}

	@Override
	public IEtcSiteView getView() {
		return view;
	}

	@Override
	public void onHome() {
		placeController.goTo(new HomePlace());
	}

	@Override
	public void onAbout() {
		placeController.goTo(new AboutPlace());
	}

	@Override
	public void onNews() {
		placeController.goTo(new NewsPlace());
	}

	@Override
	public void onTaskManager() {
		placeController.goTo(new TaskManagerPlace());
	}

	@Override
	public void onFileManager() {
		placeController.goTo(new FileManagerPlace());
	}

	@Override
	public void onAccount() {
		placeController.goTo(new SettingsPlace());
	}

	@Override
	public void onTextCapture() {
		placeController.goTo(new SemanticMarkupInputPlace());
	}

	@Override
	public void onMatrixGeneration() {
		placeController.goTo(new MatrixGenerationInputPlace());
	}

	@Override
	public void onTreeGeneration() {
		placeController.goTo(new TreeGenerationInputPlace());
	}

	@Override
	public void onTaxonomyComparison() {
		placeController.goTo(new TaxonomyComparisonPlace());
	}

	@Override
	public void onVisualization() {
		placeController.goTo(new VisualizationPlace());
	}
}
