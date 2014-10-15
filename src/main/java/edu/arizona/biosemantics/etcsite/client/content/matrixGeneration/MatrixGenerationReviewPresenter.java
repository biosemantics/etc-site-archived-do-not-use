package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.IReviewView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.IReviewView.Presenter;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;

public class MatrixGenerationReviewPresenter implements IMatrixGenerationReviewView.Presenter {

	private Task task;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private IMatrixGenerationReviewView view;
	private PlaceController placeController;
	private IReviewView.Presenter reviewPresenter;

	@Inject
	public MatrixGenerationReviewPresenter(IMatrixGenerationReviewView view, 
			IMatrixGenerationServiceAsync matrixGenerationService,
			PlaceController placeController, 
			IReviewView.Presenter reviewPresenter) {
		this.view = view;
		this.view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
		this.reviewPresenter = reviewPresenter;
	}
	
	@Override
	public void onNext() {
		if(task != null) {
			Alerter.startLoading();
			matrixGenerationService.save(Authentication.getInstance().getToken(), reviewPresenter.getTaxonMatrix(), task, new AsyncCallback<Void>() {
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
		reviewPresenter.refresh(task);
	}
}
