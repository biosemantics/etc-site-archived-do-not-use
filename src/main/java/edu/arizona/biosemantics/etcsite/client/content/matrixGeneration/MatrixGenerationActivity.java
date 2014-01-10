package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum;

public class MatrixGenerationActivity extends MyAbstractActivity {

	private ITaskServiceAsync taskService;
	private IMatrixGenerationInputView.Presenter inputPresenter;
	private IMatrixGenerationProcessView.Presenter processPresenter;
	private IMatrixGenerationReviewView.Presenter reviewPresenter;
	private IMatrixGenerationOutputView.Presenter outputPresenter;
	private AcceptsOneWidget panel;
	private Task task;

	@Inject
	public MatrixGenerationActivity(ITaskServiceAsync taskService, 
			IMatrixGenerationInputView.Presenter inputPresenter, 
			IMatrixGenerationProcessView.Presenter processPresenter,
			IMatrixGenerationReviewView.Presenter reviewPresenter,
			IMatrixGenerationOutputView.Presenter outputPresenter) {
		this.taskService = taskService;
		this.inputPresenter = inputPresenter;
		this.processPresenter = processPresenter;
		this.reviewPresenter = reviewPresenter;
		this.outputPresenter = outputPresenter;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		this.setStepWidget();
	}

	@Override
	public void update() {
		this.setStepWidget();
	}
	
	public void setTask(Task task) {
		this.task = task;
	}

	private void setStepWidget() {
		if(task == null) 
			panel.setWidget(inputPresenter.getView());
		else 
			this.taskService.getTask(Authentication.getInstance().getToken(),
					 task, new RPCCallback<Task>() {
						@Override
						public void onResult(Task result) {
							if(result.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.MATRIX_GENERATION)) {
								switch(TaskStageEnum.valueOf(result.getTaskStage().getTaskStage())) {
								case INPUT:
									panel.setWidget(inputPresenter.getView());
									break;
								case OUTPUT:
									outputPresenter.setTask(task);
									panel.setWidget(outputPresenter.getView());
									break;
								case PROCESS:
									processPresenter.setTask(task);
									panel.setWidget(processPresenter.getView());
									break;
								case REVIEW:
									reviewPresenter.setTask(task);
									panel.setWidget(reviewPresenter.getView());
									break;
								default:
									panel.setWidget(inputPresenter.getView());
									break;
								}
							}
						}
			});
	}


}
