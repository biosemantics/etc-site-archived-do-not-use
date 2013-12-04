package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.ReviewMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.ReviewMatrixGenerationViewImpl;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.review.ViewImpl;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Matrix;

public class ReviewMatrixGenerationPresenter implements ReviewMatrixGenerationView.Presenter {

	private HandlerManager eventBus;
	private ReviewMatrixGenerationView view;
	private Task task;
	private ReviewMatrixPresenter reviewMatrixPresenter;

	public ReviewMatrixGenerationPresenter(HandlerManager eventBus,	IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		
		ViewImpl viewImpl = new ViewImpl();
		reviewMatrixPresenter = new ReviewMatrixPresenter(viewImpl, matrixGenerationService);
		view = new ReviewMatrixGenerationViewImpl(viewImpl);
		view.setPresenter(this);
	}

	public void go(final HasWidgets container, final Task task) {
		reviewMatrixPresenter.setTask(task);
		reviewMatrixPresenter.refresh();
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void onNext() {
		eventBus.fireEvent(new MatrixGenerationEvent(task));
	}

}
