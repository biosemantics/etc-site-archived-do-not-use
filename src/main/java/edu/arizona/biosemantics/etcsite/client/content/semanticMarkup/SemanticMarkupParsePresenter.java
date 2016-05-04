package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

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
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class SemanticMarkupParsePresenter implements ISemanticMarkupParseView.Presenter {

	private ISemanticMarkupParseView view;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private Task task;
	private PlaceController placeController;
	
	@Inject
	public SemanticMarkupParsePresenter(final ISemanticMarkupParseView view, 
			ISemanticMarkupServiceAsync semanticMarkupService, 
			final PlaceController placeController, 
			@Named("EtcSite") final EventBus eventBus) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.semanticMarkupService = semanticMarkupService;
		this.placeController = placeController;
		
		view.setNonResumable();
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
					TaskStageEnum failedTaskStageEnum = TaskStageEnum.valueOf(failedTask.getTaskStage().getTaskStage());
					if(failedTaskStageEnum.equals(TaskStageEnum.PARSE_TEXT)) {
						MessageBox alert;
						if (failedTask.isTooLong()){
							alert = Alerter.semanticMarkupTookTooLong(null);
						} else {
							alert = Alerter.failedToParse(null);
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
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.OUTPUT,
				new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new SemanticMarkupOutputPlace(task));
			}
			@Override
			public void onFailure(Throwable caught) { 
				Alerter.failedToGoToTaskStage(caught);
			}
		});
	}

	@Override
	public void onTaskManager() {
		placeController.goTo(new TaskManagerPlace());
	}

	@Override
	public ISemanticMarkupParseView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		view.setNonResumable();
		this.task = task;
		semanticMarkupService.parse(Authentication.getInstance().getToken(), 
			task, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				//SemanticMarkupParsePresenter.this.task = result;
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}

}