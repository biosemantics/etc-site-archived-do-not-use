package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class TreeGenerationViewPresenter implements ITreeGenerationViewView.Presenter {

	private Task task;
	private ITreeGenerationServiceAsync treeGenerationService;
	private ITreeGenerationViewView view;
	private PlaceController placeController;

	@Inject
	public TreeGenerationViewPresenter(ITreeGenerationViewView view, 
			ITreeGenerationServiceAsync treeGenerationService,
			PlaceController placeController) {
		this.view = view;
		this.view.setPresenter(this);
		this.treeGenerationService = treeGenerationService;
		this.placeController = placeController;
	}
	
	@Override
	public ITreeGenerationViewView getView() {
		return view;
	}

	@Override
	public void setTask(final Task task) {
		this.task = task;
		final MessageBox box = Alerter.startLoading();
		treeGenerationService.view(Authentication.getInstance().getToken(), 
				task, new AsyncCallback<TaxonMatrix>() {
			@Override
			public void onSuccess(TaxonMatrix taxonMatrix) {
				view.getKeyView().initialize(taxonMatrix);
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToViewKey(caught);
				Alerter.stopLoading(box);
			}
		});
	}
}
