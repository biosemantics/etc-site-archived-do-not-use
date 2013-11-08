package edu.arizona.sirls.etc.site.client.presenter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
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
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IUserServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.TaxonomyComparisonTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.TreeGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.VisualizationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.Share;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskManagerPresenter implements TaskManagerView.Presenter, Presenter {

	private HandlerManager eventBus;
	private TaskManagerView view;
	private ITaskServiceAsync taskService;
	private IUserServiceAsync userService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private IVisualizationServiceAsync visualizationService;
	//private HashMap<Integer, Integer> taskRowMap = new HashMap<Integer, Integer>();

	public TaskManagerPresenter(HandlerManager eventBus, TaskManagerView view,
			ITaskServiceAsync taskService, IMatrixGenerationServiceAsync matrixGenerationService, 
			ITreeGenerationServiceAsync treeGenerationService, ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IVisualizationServiceAsync visualizationService, IUserServiceAsync userService) {
		this.view = view;
		this.taskService = taskService;
		this.matrixGenerationService = matrixGenerationService;
		this.treeGenerationService = treeGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.visualizationService = visualizationService;
		this.userService = userService;
		this.eventBus = eventBus;
		view.setPresenter(this);
	}


	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		
		taskService.getAllTasks(Authentication.getInstance().getAuthenticationToken(), new AsyncCallback<RPCResult<List<Task>>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<List<Task>> result) {
				if(result.isSucceeded())
					view.setTasks(result.getData());
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
			matrixGenerationService.cancel(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Void>>() {
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
		matrixGenerationService.getMatrixGenerationTaskRun(Authentication.getInstance().getAuthenticationToken(), 
				task, new AsyncCallback<RPCResult<MatrixGenerationTaskRun>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(RPCResult<MatrixGenerationTaskRun> matrixGenerationTaskResult) {
						//could also have a popup here asking for a new task name to use..										
						
						//pickup again from review
						if(matrixGenerationTaskResult.isSucceeded()) { 
							MatrixGenerationTaskRun matrixGenerationTask = matrixGenerationTaskResult.getData();
							matrixGenerationService.goToTaskStage(Authentication.getInstance().getAuthenticationToken(), matrixGenerationTask, 
									TaskStageEnum.REVIEW_TERMS ,new AsyncCallback<RPCResult<MatrixGenerationTaskRun>>() {
								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}
								@Override
								public void onSuccess(RPCResult<MatrixGenerationTaskRun> matrixGenerationTask) {
									if(matrixGenerationTask.isSucceeded())
										eventBus.fireEvent(new MatrixGenerationEvent(matrixGenerationTask.getData()));
								}
							});
						}
					}
		});
	}


	@Override
	public void onResume(final Task task) {
		switch(task.getTaskStage().getTaskType().getTaskTypeEnum()) {
		case MATRIX_GENERATION:
			matrixGenerationService.getMatrixGenerationTaskRun(Authentication.getInstance().getAuthenticationToken(), 
					task, new AsyncCallback<RPCResult<MatrixGenerationTaskRun>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(RPCResult<MatrixGenerationTaskRun> result) {
							if(result.isSucceeded())
								eventBus.fireEvent(new MatrixGenerationEvent(result.getData()));
						}
			});
			break;
		case TREE_GENERATION:
			treeGenerationService.getTreeGenerationTask(Authentication.getInstance().getAuthenticationToken(), 
					task, new AsyncCallback<RPCResult<TreeGenerationTaskRun>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(RPCResult<TreeGenerationTaskRun> result) {
							if(result.isSucceeded())
								eventBus.fireEvent(new TreeGenerationEvent(result.getData()));
						}
			});
			break;
		case TAXONOMY_COMPARISON:
			taxonomyComparisonService.getTaxonomyComparisonTask(Authentication.getInstance().getAuthenticationToken(), 
					task, new AsyncCallback<RPCResult<TaxonomyComparisonTaskRun>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(RPCResult<TaxonomyComparisonTaskRun> result) {
							eventBus.fireEvent(new TaxonomyComparisonEvent(result.getData()));
						}
			});
			break;
		case VISUALIZATION:
			visualizationService.getVisualizationTask(Authentication.getInstance().getAuthenticationToken(), 
					task, new AsyncCallback<RPCResult<VisualizationTaskRun>>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(RPCResult<VisualizationTaskRun> result) {
							if(result.isSucceeded())
								eventBus.fireEvent(new VisualizationEvent(result.getData()));
						}
			});
			break;
		}
	}
}
