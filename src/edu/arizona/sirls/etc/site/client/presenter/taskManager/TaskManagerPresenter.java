package edu.arizona.sirls.etc.site.client.presenter.taskManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaxonomyComparisonEvent;
import edu.arizona.sirls.etc.site.client.event.TreeGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.VisualizationEvent;
import edu.arizona.sirls.etc.site.client.presenter.MessageConfirmCancelPresenter;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.presenter.users.UserSelectPresenter;
import edu.arizona.sirls.etc.site.client.presenter.users.UsersPresenter;
import edu.arizona.sirls.etc.site.client.presenter.users.UserSelectPresenter.ISelectHandler;
import edu.arizona.sirls.etc.site.client.view.MessageConfirmCancelView;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.taskManager.TaskData;
import edu.arizona.sirls.etc.site.client.view.taskManager.TaskManagerView;
import edu.arizona.sirls.etc.site.client.view.users.UserSelectView;
import edu.arizona.sirls.etc.site.client.view.users.UserSelectViewImpl;
import edu.arizona.sirls.etc.site.client.view.users.UsersViewImpl;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IUserServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Share;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.TaskStageEnum;

public class TaskManagerPresenter implements TaskManagerView.Presenter, Presenter {

	private HandlerManager eventBus;
	private TaskManagerView view;
	private ITaskServiceAsync taskService;
	private IUserServiceAsync userService;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private IVisualizationServiceAsync visualizationService;
	//private HashMap<Integer, Integer> taskRowMap = new HashMap<Integer, Integer>();
	private Map<Task, Set<ShortUser>> inviteesForOwnedTasks = new HashMap<Task, Set<ShortUser>>();
	private MessageConfirmCancelView messageView = new MessageConfirmCancelView();
	private MessageConfirmCancelPresenter messagePresenter;

	public TaskManagerPresenter(HandlerManager eventBus, TaskManagerView view,
			ITaskServiceAsync taskService, ISemanticMarkupServiceAsync semanticMarkupService, IMatrixGenerationServiceAsync matrixGenerationService,
			ITreeGenerationServiceAsync treeGenerationService, ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IVisualizationServiceAsync visualizationService, IUserServiceAsync userService) {
		this.view = view;
		this.taskService = taskService;
		this.semanticMarkupService = semanticMarkupService;
		this.treeGenerationService = treeGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.visualizationService = visualizationService;
		this.userService = userService;
		this.eventBus = eventBus;
		view.setPresenter(this);
	}


	@Override
	public void go(HasWidgets container) {
		view.resetSelection();
		
		container.clear();
		container.add(view.asWidget());
		
		taskService.getAllTasks(Authentication.getInstance().getAuthenticationToken(), new AsyncCallback<RPCResult<List<Task>>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(final RPCResult<List<Task>> taskResult) {
				if(taskResult.isSucceeded()) {
					taskService.getInviteesForOwnedTasks(Authentication.getInstance().getAuthenticationToken(), new AsyncCallback<RPCResult<Map<Task, Set<ShortUser>>>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(RPCResult<Map<Task, Set<ShortUser>>> result) {
							if(result.isSucceeded()) { 
								inviteesForOwnedTasks = result.getData();
								List<TaskData> taskData = new LinkedList<TaskData>();
								for(Task task : taskResult.getData())
									taskData.add(new TaskData(task, result.getData().get(task)));
								view.setTaskData(taskData);
							}
						}
					});
				}
			}
		});
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				for(Task task : resumableTasksEvent.getTasks().values())
					view.updateTaskData(new TaskData(task, inviteesForOwnedTasks.get(task)));
			}
		});
	}


	@Override
	public void onShare(final TaskData taskData) {
		final Task task = taskData.getTask();
		final Share share = new Share();
		share.setTask(task);
		
		final TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(false, "Select user");
		final UsersViewImpl usersView = new UsersViewImpl();
		UsersPresenter usersPresenter = new UsersPresenter(eventBus, usersView, userService);
		taskService.getInvitees(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Set<ShortUser>>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<Set<ShortUser>> result) {
				if(result.isSucceeded())
					usersView.setSelectedUsers(result.getData());
				UserSelectView userSelectView = new UserSelectViewImpl(usersView);
				UserSelectPresenter userSelectPresenter = new UserSelectPresenter(eventBus, userSelectView, new ISelectHandler() {
					@Override
					public void onSelect(Set<ShortUser> users) {
						inviteesForOwnedTasks.put(task, users);
						view.updateTaskData(new TaskData(task, users));
						dialogBox.hide();
						
						share.setInvitees(users);
						taskService.addOrUpdateShare(Authentication.getInstance().getAuthenticationToken(), share, new AsyncCallback<RPCResult<Share>>() {
							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
							@Override
							public void onSuccess(RPCResult<Share> result) { }
						});
					} 
				});
				dialogBox.setWidget(userSelectView.asWidget());
				dialogBox.center();
				dialogBox.setGlassEnabled(true);
		 		dialogBox.show();
			}
		});
	}

	private void cancelTask(final TaskData taskData) {
		taskService.cancelTask(Authentication.getInstance().getAuthenticationToken(), taskData.getTask(), new AsyncCallback<RPCResult<Void>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(RPCResult<Void> result) {
				if(result.isSucceeded()) {
					inviteesForOwnedTasks.remove(taskData.getTask());
					view.removeTaskData(taskData);
				}
			}
		});
	}

	@Override
	public void onDelete(final TaskData taskData) {
		final Task task = taskData.getTask();
		if(task.getUser().getName().equals(Authentication.getInstance().getUsername())) {
			if(!taskData.getInvitees().isEmpty()) {
				//bring up popup asking
				if(messagePresenter == null)
					messagePresenter = new MessageConfirmCancelPresenter(messageView, "Format Requirements", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							cancelTask(taskData);
							view.removeTaskData(taskData);
							view.resetSelection();
						}
					});
				messagePresenter.setMessage("If you delete this task it will be removed for all invitees of the share. Do you want to continue?");
				messagePresenter.go();
			} else {
				cancelTask(taskData);
			}
		} else {
			taskService.removeMeFromShare(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Void>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(RPCResult<Void> result) {
					inviteesForOwnedTasks.remove(task);
					view.removeTaskData(taskData);
				} 
			});
		}
	}


	@Override
	public void onRewind(final TaskData taskData) {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getAuthenticationToken(), taskData.getTask(), 
				TaskStageEnum.REVIEW_TERMS ,new AsyncCallback<RPCResult<Task>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<Task> semanticMarkupTask) {
				if(semanticMarkupTask.isSucceeded())
					eventBus.fireEvent(new SemanticMarkupEvent(semanticMarkupTask.getData()));
			}
		});
	}


	@Override
	public void onResume(final TaskData taskData) {
		switch(taskData.getTask().getTaskType().getTaskTypeEnum()) {
		case SEMANTIC_MARKUP:
			eventBus.fireEvent(new SemanticMarkupEvent(taskData.getTask()));
			break;
		case MATRIX_GENERATION:
			eventBus.fireEvent(new MatrixGenerationEvent(taskData.getTask()));
			break;
		case TREE_GENERATION:
			eventBus.fireEvent(new TreeGenerationEvent(taskData.getTask()));
			break;
		case TAXONOMY_COMPARISON:
			eventBus.fireEvent(new TaxonomyComparisonEvent(taskData.getTask()));
			break;
		case VISUALIZATION:
			eventBus.fireEvent(new VisualizationEvent(taskData.getTask()));
			break;
		}
	}
}
