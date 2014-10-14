package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;

public class SemanticMarkupParsePresenter implements ISemanticMarkupParseView.Presenter {

	private ISemanticMarkupParseView view;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private Task task;
	private PlaceController placeController;
	
	@Inject
	public SemanticMarkupParsePresenter(final ISemanticMarkupParseView view, 
			ISemanticMarkupServiceAsync semanticMarkupService, 
			PlaceController placeController, 
			@Named("Tasks") final EventBus tasksBus) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.semanticMarkupService = semanticMarkupService;
		this.placeController = placeController;
		
		view.setNonResumable();
		tasksBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
	}

	@Override
	public void onNext() {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.OUTPUT,
				new AsyncCallback<Task>() {
		//semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.TO_ONTOLOGIES, new RPCCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new SemanticMarkupToOntologiesPlace(task));
			}
			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof SemanticMarkupException) {
					placeController.goTo(new TaskManagerPlace());
				}
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
		this.task = task;
		view.setNonResumable();
		semanticMarkupService.parse(Authentication.getInstance().getToken(), 
			task, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				//MatrixGenerationProcessPresenter.this.task = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof SemanticMarkupException) {
					placeController.goTo(new TaskManagerPlace());
				}
				Alerter.failedToParse(caught);
			}
		});
	}

}