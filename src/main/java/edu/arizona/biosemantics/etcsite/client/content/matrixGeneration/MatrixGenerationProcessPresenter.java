package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;

public class MatrixGenerationProcessPresenter implements IMatrixGenerationProcessView.Presenter {

	private IMatrixGenerationProcessView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private Task task;
	private PlaceController placeController;
	
	@Inject
	public MatrixGenerationProcessPresenter(final IMatrixGenerationProcessView view, 
			IMatrixGenerationServiceAsync matrixGenerationService, 
			final PlaceController placeController, 
			@Named("EtcSite") final EventBus eventBus) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
		eventBus.addHandler(FailedTasksEvent.TYPE, new FailedTasksEvent.FailedTasksEventHandler() {
			@Override
			public void onFailedTasksEvent(FailedTasksEvent failedTasksEvent) {
				if(task != null && failedTasksEvent.getTasks().containsKey(task.getId())) {
					Task failedTask = failedTasksEvent.getTasks().get(task.getId());
					TaskStageEnum failedtaskStageEnum = TaskStageEnum.valueOf(failedTask.getTaskStage().getTaskStage());
					if(failedtaskStageEnum.equals(TaskStageEnum.PROCESS)) {
						MessageBox alert;
						if (failedTask.isTooLong()){
							alert = Alerter.matrixGenerationTookTooLong(null);
						} else {
							alert = Alerter.failedToGenerateMatrix(null);
						}
						alert.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
							@Override
							public void onSelect(SelectEvent event) {
								placeController.goTo(new TaskManagerPlace());
							}
						});
					}
				}
			}
		});
	}

	@Override
	public void onNext() {
		placeController.goTo(new MatrixGenerationReviewPlace(task));
	}

	@Override
	public void onTaskManager() {
		placeController.goTo(new TaskManagerPlace());
	}

	@Override
	public IMatrixGenerationProcessView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		view.setNonResumable();
		this.task = task;
		matrixGenerationService.process(Authentication.getInstance().getToken(), 
			task, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				MatrixGenerationProcessPresenter.this.task = result;
			}
			@Override
			public void onFailure(Throwable caught) { }
		});
	}
}
