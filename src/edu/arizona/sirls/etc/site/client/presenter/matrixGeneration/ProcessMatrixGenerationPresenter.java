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
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.LearnInvocation;

public class ProcessMatrixGenerationPresenter implements ProcessMatrixGenerationView.Presenter {

	private HandlerManager eventBus;
	private ProcessMatrixGenerationView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private Task task;

	public ProcessMatrixGenerationPresenter(HandlerManager eventBus, ProcessMatrixGenerationView view, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
	}

	public void go(final HasWidgets container, final Task task) {
		loadingPopup.start();
		view.setNonResumable();
		this.task = task;
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(resumableTasksEvent.getTasks().containsKey(task.getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
		
		matrixGenerationService.process(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Task>>() { 
			public void onSuccess(RPCResult<Task> result) {
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
		eventBus.fireEvent(new MatrixGenerationEvent(task));
	}

	@Override
	public void onTaskManager() {
		eventBus.fireEvent(new TaskManagerEvent());
	}

}
