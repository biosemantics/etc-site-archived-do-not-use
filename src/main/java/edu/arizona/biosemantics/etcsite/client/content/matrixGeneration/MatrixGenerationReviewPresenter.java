package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class MatrixGenerationReviewPresenter implements IMatrixGenerationReviewView.Presenter {

	private Task task;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private IMatrixGenerationReviewView view;
	private PlaceController placeController;
	private Model model;

	@Inject
	public MatrixGenerationReviewPresenter(final IMatrixGenerationReviewView view, 
			final IMatrixGenerationServiceAsync matrixGenerationService,
			PlaceController placeController) {
		this.view = view;
		this.view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
		
		view.getMatrixReviewView().setSaveHandler(new SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				Alerter.startLoading();
				matrixGenerationService.outputMatrix(Authentication.getInstance().getToken(), 
						task, event.getModel(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						Alerter.stopLoading();
						Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
								"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToOutputMatrix(caught);
					}
				});
			}
		});
	}
	
	@Override
	public void onNext() {
		if(task != null) {
			Alerter.startLoading();
			matrixGenerationService.save(Authentication.getInstance().getToken(), model, task, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) { 
					matrixGenerationService.completeReview(Authentication.getInstance().getToken(), 
							task, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {	
							Alerter.stopLoading();
							placeController.goTo(new MatrixGenerationOutputPlace(result));
						}
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToCompleteReview(caught);
							Alerter.stopLoading();
						}
					});
				}

				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToSaveMatrixGeneration(caught);
					Alerter.stopLoading();
				}
			});
		}
	}

	@Override
	public IMatrixGenerationReviewView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		this.task = task;
		Alerter.startLoading();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new AsyncCallback<Model>() { 
			public void onSuccess(Model result) {
				model = result;
				TaxonMatrix taxonMatrix = model.getTaxonMatrix();
				view.setFullModel(model);
				Alerter.stopLoading();
				if (taxonMatrix.getCharacterCount() == 0 || (taxonMatrix.getCharacterCount() == 1 && taxonMatrix.getFlatCharacters().get(0).getName().equals(""))){
					Alerter.matrixGeneratedEmpty();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToReview(caught);
				Alerter.stopLoading();
			}
		}); 
	}
	
	@Override
	public Model getTaxonMatrix() {
		return model;
	}
}
