package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class ReviewPresenter implements IReviewView.Presenter {

	private IReviewView view;
	private MatrixReviewView matrixReviewView = new MatrixReviewView();
	private Model model;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private Task task;
	private PlaceController placeController;
	
	@Inject
	public ReviewPresenter(IReviewView view, final IMatrixGenerationServiceAsync matrixGenerationService, 
			 PlaceController placeController) {
		this.matrixGenerationService = matrixGenerationService;
		this.view = view;
		this.placeController = placeController;
		matrixReviewView.setSaveHandler(new SaveHandler() {
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
	public void refresh(Task task) {
		this.task = task;
		Alerter.startLoading();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new AsyncCallback<Model>() { 
			public void onSuccess(Model result) {
				model = result;
				TaxonMatrix taxonMatrix = model.getTaxonMatrix();
				matrixReviewView.setFullModel(model);
				view.setMatrixReviewView(matrixReviewView);
				Alerter.stopLoading();
				if (taxonMatrix.getCharacterCount() == 0 || (taxonMatrix.getCharacterCount() == 1 && taxonMatrix.getFlatCharacters().get(0).getName().equals(""))){
					Alerter.matrixGeneratedEmpty();}
			}
			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof MatrixGenerationException)
					placeController.goTo(new TaskManagerPlace());
				Alerter.failedToReview(caught);
			}
		}); 
		
	}
	
	@Override
	public IReviewView getView() {
		return view;
	}

	@Override
	public Model getTaxonMatrix() {
		return model;
	}

}

