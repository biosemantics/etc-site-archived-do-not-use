package edu.arizona.biosemantics.etcsite.client.common;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;

import edu.arizona.biosemantics.etcsite.client.common.TaskSelectDialog.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeBuildPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupLearnPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupParsePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPreprocessPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonAlignPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationViewPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeServiceAsync;
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
	private IOntologizeServiceAsync ontologizeService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;

	@Inject
	public ResumeTaskToPlaceGoer(PlaceController placeController, ITaskServiceAsync taskService, 
			ISemanticMarkupServiceAsync semanticMarkupService, IMatrixGenerationServiceAsync matrixGenerationService, 
			ITreeGenerationServiceAsync treeGenerationService, ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IOntologizeServiceAsync ontologizeService) {
		this.placeController = placeController;
		this.taskService = taskService;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.treeGenerationService = treeGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.ontologizeService = ontologizeService;
	}
	
	@Override
	public void goTo(final Place newPlace, final LinkedList<ToPlaceGoer> nextToPlaceGoers) {
		if(newPlace instanceof HasTaskPlace) {
			final HasTaskPlace hasTaskPlace = (HasTaskPlace)newPlace;
			IHasTasksServiceAsync hasTasksService = getTasksService(hasTaskPlace);		
			if(hasTasksService != null) {
				hasTasksService.getResumables(Authentication.getInstance().getToken(), new AsyncCallback<List<Task>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToGetLatestResumable(caught);
					}
					@Override
					public void onSuccess(List<Task> result) {
						/*if(!result.isEmpty()) {
							TaskSelectDialog taskDialog = new TaskSelectDialog(SelectionMode.SINGLE);
							taskDialog.getButton(PredefinedButton.OK).setText("Resume selected task");
							taskDialog.getButton(PredefinedButton.CANCEL).setText("Start new task");
							taskDialog.show(result, new ISelectListener() {
								@Override
								public void onSelect(List<Task> selection) {
									HasTaskPlace taskPlace = getTaskPlace(selection.get(0));
									doGoTo(taskPlace, nextToPlaceGoers);
								}
								@Override
								public void onCancel() {
									doGoTo(hasTaskPlace, nextToPlaceGoers);
								}
							});
						} else {*/
					    doGoTo(hasTaskPlace, nextToPlaceGoers);
						//}
					}
				});
				
				/*hasTasksService.getLatestResumable(Authentication.getInstance().getToken(),
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
				});*/
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
		if(newPlace instanceof OntologizePlace)
			return this.ontologizeService;
		return null;
	}

	private void doGoTo(Place newPlace, LinkedList<ToPlaceGoer> nextToPlaceGoers) {
		if(nextToPlaceGoers == null || nextToPlaceGoers.isEmpty())
			placeController.goTo(newPlace);
		else
			nextToPlaceGoers.removeFirst().goTo(newPlace, nextToPlaceGoers);
	}
	
	private HasTaskPlace getTaskPlace(Task task){
		String taskStage = task.getTaskStage().getDisplayName();
		String taskType = task.getTaskType().getTaskTypeEnum().displayName();
		switch(taskType){
		case "Text Capture":
			switch(taskStage){
			case "Create Input":
				return new SemanticMarkupInputPlace(task);
			case "Input":
				return new SemanticMarkupDefinePlace(task);
			case "Preprocess Text":
				return new SemanticMarkupPreprocessPlace(task);
			case "Learn Terms":
				return new SemanticMarkupLearnPlace(task);
			case "Review Terms":
				return new SemanticMarkupReviewPlace(task);
			case "Parse Text":
				return new SemanticMarkupParsePlace(task);
			case "Output":
				return new SemanticMarkupOutputPlace(task);
			}
		case "Ontology Building":
			switch(taskStage){
			case "Create Input":
				return new OntologizeInputPlace(task);
			case "Input":
				return new OntologizeDefinePlace(task);
			case "Build":
				return new OntologizeBuildPlace(task);
			}
		case "Matrix Generation":
			switch(taskStage){
			case "Create/Select Input":
				return new MatrixGenerationInputPlace(task);
			case "Input":
				return new MatrixGenerationDefinePlace(task);
			case "Process":
				return new MatrixGenerationProcessPlace(task);
			case "Review":
				return new MatrixGenerationReviewPlace(task);
			case "Output":
				return new MatrixGenerationOutputPlace(task);
			}
		case "Key Generation":
			switch(taskStage){
			case "Create Input":
				return new TreeGenerationInputPlace(task);
			case "Input":
				return new TreeGenerationDefinePlace(task);
			case "View":
				return new TreeGenerationViewPlace(task);
			}
		case "Taxonomy Comparison":
			switch(taskStage){
			case "Create Input":
				return new TaxonomyComparisonInputPlace(task);
			case "Input":
				return new TaxonomyComparisonDefinePlace(task);
			case "Align":
			case "Analyze": 
			case "Analyze Complete":
				return new TaxonomyComparisonAlignPlace(task);
			}
		case "Visualization":
			return new VisualizationPlace(task);
		}
		return null;
	}

}
