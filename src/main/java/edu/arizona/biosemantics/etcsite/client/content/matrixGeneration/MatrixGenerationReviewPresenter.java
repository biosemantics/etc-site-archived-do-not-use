package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.IReviewView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.IReviewView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

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
		matrixGenerationService.completeReview(Authentication.getInstance().getToken(), 
				task, new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {	
				placeController.goTo(new MatrixGenerationOutputPlace(result));
			}
		});
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
