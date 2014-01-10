package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MessageConfirmPresenter;
import edu.arizona.biosemantics.etcsite.client.common.MessageConfirmPresenter.AbstractConfirmListener;
import edu.arizona.biosemantics.etcsite.client.content.user.IUserSelectView;
import edu.arizona.biosemantics.etcsite.client.content.user.IUsersView;
import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectPresenter;
import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectPresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.user.UsersPresenter;
import edu.arizona.biosemantics.etcsite.shared.db.Share;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class TaskManagerPresenter implements ITaskManagerView.Presenter {

	private IUserSelectView.Presenter userSelectPresenter;
	private IUsersView.Presenter usersPresenter;
	private Map<Task, Set<ShortUser>> inviteesForOwnedTasks = new HashMap<Task, Set<ShortUser>>();
	private MessageConfirmPresenter messagePresenter;
	private ITaskServiceAsync taskService;
	private ITaskManagerView view;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private PlaceController placeController;
	private EventBus eventBus;
	private ResumeTaskPlaceMapper resumeTaskPlaceMapper;
	
	@Inject 
	public TaskManagerPresenter(final ITaskManagerView view, PlaceController placeController, @Named("Tasks")EventBus eventBus,
			MessageConfirmPresenter messagePresenter, final ITaskServiceAsync taskService, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IMatrixGenerationServiceAsync matrixGenerationService, ResumeTaskPlaceMapper resumeTaskPlaceMapper, 
			IUserSelectView.Presenter userSelectPresenter, IUsersView.Presenter usersPresenter) {
		this.view = view;
		this.view.setPresenter(this);
		this.placeController = placeController;
		this.eventBus = eventBus;
		this.userSelectPresenter = userSelectPresenter;
		this.usersPresenter = usersPresenter;
		this.messagePresenter = messagePresenter;
		this.taskService = taskService;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.resumeTaskPlaceMapper = resumeTaskPlaceMapper;
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				for(Task task : resumableTasksEvent.getTasks().values()) {
					view.updateTaskData(new TaskData(task, inviteesForOwnedTasks.get(task)));
				}
			}
		});
	}

	@Override
	public void onShare(TaskData taskData) {
		final Task task = taskData.getTask();
		final Share share = new Share();
		share.setTask(task);
		taskService.getInvitees(Authentication.getInstance().getToken(), task, new RPCCallback<Set<ShortUser>>() {			
			@Override
			public void onResult(Set<ShortUser> result) {
				usersPresenter.refresh();
				//usersPresenter.setSelected(result);
				userSelectPresenter.show(new ISelectListener() {
					@Override
					public void onSelect(Set<ShortUser> users) {
						inviteesForOwnedTasks.put(task, users);
						view.updateTaskData(new TaskData(task, users));						
						share.setInvitees(users);
						taskService.addOrUpdateShare(Authentication.getInstance().getToken(), share, new RPCCallback<Share>() {
							@Override
							public void onResult(Share result) {} 				
						});
					}
				});
			}
		});
	}

	@Override
	public void onDelete(final TaskData taskData) {
		final Task task = taskData.getTask();
		if(task.getUser().getName().equals(Authentication.getInstance().getUsername())) {
			if(!taskData.getInvitees().isEmpty()) {
				//bring up popup asking
				messagePresenter.show("Format Requirements", "If you delete this task it will be removed for all invitees of the share. Do you want to continue?", new AbstractConfirmListener() {
					@Override
					public void onConfirm() {
						cancelTask(taskData);
						view.resetSelection();
					}
				});
			} else {
				cancelTask(taskData);
				view.resetSelection();
			}
		} else {
			taskService.removeMeFromShare(Authentication.getInstance().getToken(), task, new RPCCallback<Void>() {
				@Override
				public void onResult(Void result) {
					inviteesForOwnedTasks.remove(task);
					view.removeTaskData(taskData);
				}
			});
		}
	}
	
	private void cancelTask(final TaskData taskData) {
		taskService.cancelTask(Authentication.getInstance().getToken(), taskData.getTask(), new RPCCallback<Void>() {
			@Override
			public void onResult(Void result) {
				inviteesForOwnedTasks.remove(taskData.getTask());
				view.removeTaskData(taskData);
			}
		});
	}

	@Override
	public void onRewind(final TaskData taskData) {
		switch(taskData.getTask().getTaskType().getTaskTypeEnum()) {
		case SEMANTIC_MARKUP:
			semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), taskData.getTask(), 
					edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum.REVIEW_TERMS ,new RPCCallback<Task>() {
				@Override
				public void onResult(Task result) {
					placeController.goTo(new edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPlace(result));
				}
			});
			break;
		case MATRIX_GENERATION:
			matrixGenerationService.goToTaskStage(Authentication.getInstance().getToken(), taskData.getTask(), 
					edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum.REVIEW, new RPCCallback<Task>() {
				@Override
				public void onResult(Task result) {
					placeController.goTo(new edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPlace(result));
				}
			});
			break;
		}
	}

	@Override
	public void onResume(TaskData taskData) {
		placeController.goTo(resumeTaskPlaceMapper.getPlace(taskData.getTask()));
	}

	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void refresh() {
		view.resetSelection();
		taskService.getAllTasks(Authentication.getInstance().getToken(), new RPCCallback<List<Task>>() {
			@Override
			public void onResult(final List<Task> taskResult) {
				taskService.getInviteesForOwnedTasks(Authentication.getInstance().getToken(), new RPCCallback<Map<Task, Set<ShortUser>>>() {
					@Override
					public void onResult(Map<Task, Set<ShortUser>> result) {
						inviteesForOwnedTasks = result;
						List<TaskData> taskData = new LinkedList<TaskData>();
						for(Task task : taskResult)
							taskData.add(new TaskData(task, result.get(task)));
						view.setTaskData(taskData);
					}
				});
			}
		});
	}
}