package edu.arizona.biosemantics.etcsite.client.common;

import java.util.LinkedList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;

public class ResumeTaskToPlaceGoer implements ToPlaceGoer {

	private PlaceController placeController;
	private ITaskServiceAsync taskService;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;

	@Inject
	public ResumeTaskToPlaceGoer(PlaceController placeController, ITaskServiceAsync taskService, 
			ISemanticMarkupServiceAsync semanticMarkupService, IMatrixGenerationServiceAsync matrixGenerationService, 
			ITreeGenerationServiceAsync treeGenerationService, ITaxonomyComparisonServiceAsync taxonomyComparisonService) {
		this.placeController = placeController;
		this.taskService = taskService;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.treeGenerationService = treeGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
	}
	
	@Override
	public void goTo(final Place newPlace, final LinkedList<ToPlaceGoer> nextToPlaceGoers) {
		if(newPlace instanceof HasTaskPlace) {
			final HasTaskPlace hasTaskPlace = (HasTaskPlace)newPlace;
			IHasTasksServiceAsync hasTasksService = getTasksService(hasTaskPlace);		
			if(hasTasksService != null) {
				hasTasksService.getLatestResumable(Authentication.getInstance().getToken(),
						new AsyncCallback<Task>() {
					@Override
					public void onSuccess(final Task task) {
						if(task != null) {
							MessageBox resumable = Alerter.resumableTask();
							resumable.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
								@Override
								public void onSelect(SelectEvent event) {
									hasTaskPlace.setTask(task);
									doGoTo(hasTaskPlace, nextToPlaceGoers);
								}
							});
							resumable.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
								@Override
								public void onSelect(SelectEvent event) {
									doGoTo(hasTaskPlace, nextToPlaceGoers);
								}
							});
						}
						else {
							doGoTo(hasTaskPlace, nextToPlaceGoers);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToGetLatestResumable(caught);
					}
				});
			}
		}
	}
	
	private IHasTasksServiceAsync getTasksService(HasTaskPlace newPlace) {
		if(newPlace instanceof SemanticMarkupPlace)
			return this.semanticMarkupService;
		if(newPlace instanceof MatrixGenerationPlace)
			return this.matrixGenerationService;
		if(newPlace instanceof TreeGenerationPlace)
			return this.treeGenerationService;
		if(newPlace instanceof TaxonomyComparisonPlace)
			return this.taxonomyComparisonService;
		return null;
	}

	private void doGoTo(Place newPlace, LinkedList<ToPlaceGoer> nextToPlaceGoers) {
		if(nextToPlaceGoers == null || nextToPlaceGoers.isEmpty())
			placeController.goTo(newPlace);
		else
			nextToPlaceGoers.removeFirst().goTo(newPlace, nextToPlaceGoers);
	}

}
