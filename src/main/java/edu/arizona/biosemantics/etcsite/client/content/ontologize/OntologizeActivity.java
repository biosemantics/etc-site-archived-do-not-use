package edu.arizona.biosemantics.etcsite.client.content.ontologize;

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
import edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;

public class OntologizeActivity extends MyAbstractActivity {

	private ITaskServiceAsync taskService;
	private IOntologizeInputView.Presenter createPresenter;
	private IOntologizeDefineView.Presenter inputPresenter;
	private IOntologizeBuildView.Presenter buildPresenter;
	private IOntologizeOutputView.Presenter outputPresenter;
	private AcceptsOneWidget panel;

	@Inject
	public OntologizeActivity(ITaskServiceAsync taskService,
			IOntologizeInputView.Presenter createPresenter,
			IOntologizeDefineView.Presenter inputPresenter,
			IOntologizeBuildView.Presenter buildPresenter,
			IOntologizeOutputView.Presenter outputPresenter,
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.taskService = taskService;
		this.createPresenter = createPresenter;
		this.inputPresenter = inputPresenter;
		this.buildPresenter = buildPresenter;
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
		if(place instanceof OntologizePlace)
			task = ((OntologizePlace)place).getTask();
		if(task == null) {
			if(place instanceof OntologizeDefinePlace) {
				inputPresenter.setSelectedFolder(createPresenter.getInputFolderPath(), createPresenter.getInputFolderShortenedPath());
				panel.setWidget(inputPresenter.getView());
			} else {
				createPresenter.refresh();
				panel.setWidget(createPresenter.getView());
			}
		} else 
			this.taskService.getTask(Authentication.getInstance().getToken(),
					 task, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {
							if(result.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.ONTOLOGIZE)) {
								switch(TaskStageEnum.valueOf(result.getTaskStage().getTaskStage())) {
								case CREATE_INPUT:
									panel.setWidget(createPresenter.getView());
									createPresenter.refresh();
									break;
								case INPUT:
									inputPresenter.setSelectedFolder(createPresenter.getInputFolderPath(), createPresenter.getInputFolderShortenedPath());
									panel.setWidget(inputPresenter.getView());
									break;
								case BUILD:
									buildPresenter.setTask(result);
									panel.setWidget(buildPresenter.getView());
									break;
								/*case OUTPUT:
									outputPresenter.setTask(result);
									panel.setWidget(outputPresenter.getView());
									break;*/
								default:
									panel.setWidget(createPresenter.getView());
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

}