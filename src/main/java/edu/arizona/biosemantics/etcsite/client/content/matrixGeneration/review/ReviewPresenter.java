package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Matrix;

public class ReviewPresenter implements IReviewView.Presenter {
	
	private IReviewView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private Task task;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	@Inject
	public ReviewPresenter(IReviewView view, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.view = view;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
	}
	
	public void onSave() {
		if(task != null)
			matrixGenerationService.save(Authentication.getInstance().getToken(), view.getMatrix(), task, new RPCCallback<Void>() {
				@Override
				public void onResult(Void result) {}
			});
	}

	@Override
	public void refresh(Task task) {
		this.task = task;
		loadingPopup.start();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new RPCCallback<Matrix>() { 
			public void onResult(Matrix result) {
				view.setMatrix(result);
				loadingPopup.stop();
			}
		});
	}

	@Override
	public IReviewView getView() {
		return view;
	}
}
