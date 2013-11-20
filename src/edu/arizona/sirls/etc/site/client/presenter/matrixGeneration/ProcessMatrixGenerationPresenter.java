package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.InputMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.ProcessMatrixGenerationView;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.LearnInvocation;

public class ProcessMatrixGenerationPresenter implements ProcessMatrixGenerationView.Presenter {

	private HandlerManager eventBus;
	private ProcessMatrixGenerationView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private MatrixGenerationTaskRun matrixGenerationTaskRun;
	private LoadingPopup loadingPopup = new LoadingPopup();

	public ProcessMatrixGenerationPresenter(HandlerManager eventBus, ProcessMatrixGenerationView view, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
	}

	public void go(final HasWidgets container, final MatrixGenerationTaskRun matrixGenerationTaskRun) {
		loadingPopup.start();
		view.setNonResumable();
		this.matrixGenerationTaskRun = matrixGenerationTaskRun;
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(resumableTasksEvent.getTasks().containsKey(matrixGenerationTaskRun.getTask().getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
		
		matrixGenerationService.process(Authentication.getInstance().getAuthenticationToken(), matrixGenerationTaskRun, new AsyncCallback<RPCResult<MatrixGenerationTaskRun>>() { 
			public void onSuccess(RPCResult<MatrixGenerationTaskRun> result) {
				if(result.isSucceeded()) {
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
		eventBus.fireEvent(new MatrixGenerationEvent(matrixGenerationTaskRun));
	}

	@Override
	public void onTaskManager() {
		eventBus.fireEvent(new TaskManagerEvent());
	}

}
