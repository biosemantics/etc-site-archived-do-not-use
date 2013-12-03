package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.ReviewMatrixGenerationView;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Matrix;

public class ReviewMatrixGenerationPresenter implements ReviewMatrixGenerationView.Presenter {

	private HandlerManager eventBus;
	private ReviewMatrixGenerationView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private Task task;
	private ReviewMatrixPresenter reviewMatrixPresenter;

	public ReviewMatrixGenerationPresenter(HandlerManager eventBus, ReviewMatrixGenerationView view, ReviewMatrixPresenter reviewMatrixPresenter, 
			IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		this.reviewMatrixPresenter = reviewMatrixPresenter;
		this.matrixGenerationService = matrixGenerationService;
	}

	public void go(final HasWidgets container, final Task task) {
		loadingPopup.start();
		this.task = task;
		
		matrixGenerationService.review(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Matrix>>() { 
			public void onSuccess(RPCResult<Matrix> result) {
				if(result.isSucceeded()) {
					Matrix matrix = result.getData();
					reviewMatrixPresenter.setData(matrix);
					container.clear();
					container.add(view.asWidget());
					loadingPopup.stop();
				}
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
		});
	}

	@Override
	public void onNext() {
		eventBus.fireEvent(new MatrixGenerationEvent(task));
	}

}
