package edu.arizona.sirls.etc.site.client.presenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import edu.arizona.sirls.etc.site.client.presenter.users.UserSelectPresenter;
import edu.arizona.sirls.etc.site.client.presenter.users.UsersPresenter;
import edu.arizona.sirls.etc.site.client.presenter.users.UserSelectPresenter.ISelectHandler;
import edu.arizona.sirls.etc.site.client.view.TaskManagerView;
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
							if(result.isSucceeded()) 
								view.setTasks(taskResult.getData(), result.getData());
						}
					});
				}
			}
		});
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				for(Task task : resumableTasksEvent.getTasks().values())
					view.updateTask(task);
			}
		});
	}


	@Override
	public void onShare(final Task task) {
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
						share.setInvitees(users);
						view.setInvitees(task, users);
						dialogBox.hide();
						
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


	@Override
	public void onDelete(final Task task) {
		if(task.getUser().getName().equals(Authentication.getInstance().getUsername())) {
			taskService.cancelTask(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Void>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(RPCResult<Void> result) {
					if(result.isSucceeded())
						view.removeTask(task);
				}
				
			});
		} else {
			taskService.removeMeFromShare(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Void>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(RPCResult<Void> result) {
					view.removeTask(task);
				} 
			});
		}
	}


	@Override
	public void onRewind(final Task task) {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getAuthenticationToken(), task, 
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
	public void onResume(final Task task) {
		switch(task.getTaskType().getTaskTypeEnum()) {
		case SEMANTIC_MARKUP:
			eventBus.fireEvent(new SemanticMarkupEvent(task));
			break;
		case MATRIX_GENERATION:
			eventBus.fireEvent(new MatrixGenerationEvent(task));
			break;
		case TREE_GENERATION:
			eventBus.fireEvent(new TreeGenerationEvent(task));
			break;
		case TAXONOMY_COMPARISON:
			eventBus.fireEvent(new TaxonomyComparisonEvent(task));
			break;
		case VISUALIZATION:
			eventBus.fireEvent(new VisualizationEvent(task));
			break;
		}
	}
}
