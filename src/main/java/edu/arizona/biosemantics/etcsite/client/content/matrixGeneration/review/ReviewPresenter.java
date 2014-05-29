package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class ReviewPresenter implements IReviewView.Presenter {

	private IReviewView view;
	private MatrixReviewView matrixReviewView;
	private TaxonMatrix taxonMatrix;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private Task task;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	@Inject
	public ReviewPresenter(IReviewView view, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.matrixGenerationService = matrixGenerationService;
		this.view = view;
	}
	
	@Override
	public void refresh(Task task) {
		this.task = task;
		loadingPopup.start();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new RPCCallback<TaxonMatrix>() { 
			public void onResult(TaxonMatrix result) {
				taxonMatrix = result;
				matrixReviewView = new MatrixReviewView(result);
				view.setMatrixReviewView(matrixReviewView);
				loadingPopup.stop();
			}
		});
		
	}
	
	public void onSave() {
		if(task != null)
			matrixGenerationService.save(Authentication.getInstance().getToken(), taxonMatrix, task, new RPCCallback<Void>() {
				@Override
				public void onResult(Void result) { }
			});
	}

	@Override
	public IReviewView getView() {
		return view;
	}

}
