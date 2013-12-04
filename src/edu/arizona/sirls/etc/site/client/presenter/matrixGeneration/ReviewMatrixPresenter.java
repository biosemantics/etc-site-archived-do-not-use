package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.review.IView;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Taxon;

public class ReviewMatrixPresenter implements IView.Presenter {

	private IView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private Task task;
	
	public ReviewMatrixPresenter(IView view, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.view = view;
		this.matrixGenerationService = matrixGenerationService;
		view.setPresenter(this);
	}

	public void go(HasWidgets container, Task task) {
		this.task = task;
		container.clear();
		container.add(view.asWidget());
	}
	
	public void onSave() {
		matrixGenerationService.save(Authentication.getInstance().getAuthenticationToken(), view.getMatrix(), task, new AsyncCallback<RPCResult<Void>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<Void> result) {}
		});
	}

	public void refresh() {
		loadingPopup.start();
		matrixGenerationService.review(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Matrix>>() { 
			public void onSuccess(RPCResult<Matrix> result) {
				if(result.isSucceeded()) {
					Matrix matrix = result.getData();
					view.setMatrix(matrix);
					loadingPopup.stop();
				}
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
		});
	}

	public void setTask(Task task) {
		this.task = task;
	}



}
