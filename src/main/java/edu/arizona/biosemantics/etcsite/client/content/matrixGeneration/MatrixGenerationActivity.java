package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;

public class MatrixGenerationActivity extends MyAbstractActivity {

	private ITaskServiceAsync taskService;
	private IMatrixGenerationInputView.Presenter inputPresenter;
	private IMatrixGenerationProcessView.Presenter processPresenter;
	private IMatrixGenerationReviewView.Presenter reviewPresenter;
	private IMatrixGenerationOutputView.Presenter outputPresenter;
	private AcceptsOneWidget panel;
	private TaskStageEnum currentTaskStage;

	@Inject
	public MatrixGenerationActivity(ITaskServiceAsync taskService, 
			IMatrixGenerationInputView.Presenter inputPresenter, 
			IMatrixGenerationProcessView.Presenter processPresenter,
			IMatrixGenerationReviewView.Presenter reviewPresenter,
			IMatrixGenerationOutputView.Presenter outputPresenter, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
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

	private void setStepWidget() {
		Task task = null;
		Place place = placeController.getWhere();
		if(place instanceof MatrixGenerationPlace)
			task = ((MatrixGenerationPlace)place).getTask();
		if(task == null) 
			panel.setWidget(inputPresenter.getView());
		else 
			this.taskService.getTask(Authentication.getInstance().getToken(),
					 task, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {
							if(result.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.MATRIX_GENERATION)) {
								currentTaskStage = TaskStageEnum.valueOf(result.getTaskStage().getTaskStage());
								switch(currentTaskStage) {
								case INPUT:
									panel.setWidget(inputPresenter.getView());
									break;
								case OUTPUT:
									outputPresenter.setTask(result);
									panel.setWidget(outputPresenter.getView());
									break;
								case PROCESS:
									processPresenter.setTask(result);
									panel.setWidget(processPresenter.getView());
									break;
								case REVIEW:
									reviewPresenter.setTask(result);
									panel.setWidget(reviewPresenter.getView());
									break;
								default:
									panel.setWidget(inputPresenter.getView());
									break;
								}
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToGetTask(caught);
						}
			});
	}
	
	@Override
	public String mayStop() {
		if(currentTaskStage != null) {
			switch(currentTaskStage) {
			case REVIEW:
				if(reviewPresenter.hasUnsavedChanges())
					return "You have unsaved changes. Do you want to continue without saving?";
			default:
				return null;
			}
		}
		return null;
	}


}
