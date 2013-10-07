package edu.arizona.sirls.etc.site.client.presenter;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaxonomyComparisonEvent;
import edu.arizona.sirls.etc.site.client.event.TreeGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.VisualizationEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskManagerPresenter {
	
	public interface Display {
		Widget asWidget();
		//FlexTable getSharedTasksTable();
		FlexTable getYourTasksTable();
		FlexTable getHistoryTable();
	}

	private HandlerManager eventBus;
	private Display display;
	private ITaskServiceAsync taskService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private IVisualizationServiceAsync visualizationService;
	private HashMap<Integer, Integer> taskRowMap = new HashMap<Integer, Integer>();

	public TaskManagerPresenter(HandlerManager eventBus,
			Display display, ITaskServiceAsync taskService, IMatrixGenerationServiceAsync matrixGenerationService, 
			ITreeGenerationServiceAsync treeGenerationService, ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IVisualizationServiceAsync visualizationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.taskService = taskService;
		this.matrixGenerationService = matrixGenerationService;
		this.treeGenerationService = treeGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.visualizationService = visualizationService;
	}

	public void go(HasWidgets content) {
		drawTable();
		content.clear();
		content.add(display.asWidget());
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				for(Task task : resumableTasksEvent.getTasks().values()) {
					Integer row = taskRowMap.get(task.getId());
					if(row != null) {
						Panel statusPanel = getStatusPanel(task);
						display.getYourTasksTable().setWidget(row, 3, statusPanel);
						Panel actionsPanel = getActionsPanel(task);
						display.getYourTasksTable().setWidget(row, 4, actionsPanel);
					}
				}
			}
		});
	}
	
	private Panel getStatusPanel(Task task) {
		HorizontalPanel statusPanel = new HorizontalPanel();
		int j = 0;
		int x = 0;
		for(TaskStageEnum step : TaskStageEnum.values()) {
			j++;
			if(step.equals(task.getTaskStage().getTaskStageEnum())) {
				x = j;
			}
		}
		statusPanel.add(new Label("Step " + x + " of " + j + ": " + task.getTaskStage().getTaskStageEnum().displayName()));
		if(!task.isResumable()) {
			Image loaderImage = new Image("images/loader3.gif");
			loaderImage.addStyleName("loader");
			statusPanel.add(loaderImage);
		}
		return statusPanel;
	}
	
	private Panel getActionsPanel(final Task task) { 
		HorizontalPanel actionsPanel = new HorizontalPanel();
		
		if(task.isResumable()) {
			Image resumeImage = new Image("images/play.png");
			resumeImage.setSize("15px", "15px");
			resumeImage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					switch(task.getTaskStage().getTaskType().getTaskTypeEnum()) {
					case MATRIX_GENERATION:
						matrixGenerationService.getMatrixGenerationConfiguration(Authentication.getInstance().getAuthenticationToken(), 
								task, new AsyncCallback<MatrixGenerationConfiguration>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(MatrixGenerationConfiguration result) {
										eventBus.fireEvent(new MatrixGenerationEvent(result));
									}
						});
						break;
					case TREE_GENERATION:
						treeGenerationService.getTreeGenerationConfiguration(Authentication.getInstance().getAuthenticationToken(), 
								task, new AsyncCallback<TreeGenerationConfiguration>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(TreeGenerationConfiguration result) {
										eventBus.fireEvent(new TreeGenerationEvent(result));
									}
						});
						break;
					case TAXONOMY_COMPARISON:
						taxonomyComparisonService.getTaxonomyComparisonConfiguration(Authentication.getInstance().getAuthenticationToken(), 
								task, new AsyncCallback<TaxonomyComparisonConfiguration>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(TaxonomyComparisonConfiguration result) {
										eventBus.fireEvent(new TaxonomyComparisonEvent(result));
									}
						});
						break;
					case VISUALIZATION:
						visualizationService.getVisualizationConfiguration(Authentication.getInstance().getAuthenticationToken(), 
								task, new AsyncCallback<VisualizationConfiguration>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(VisualizationConfiguration result) {
										eventBus.fireEvent(new VisualizationEvent(result));
									}
						});
						break;
					}
				}
			});
			actionsPanel.add(resumeImage);
		}
		
		if(task.isCompleted()) {
			switch(task.getTaskStage().getTaskType().getTaskTypeEnum()) {
			case MATRIX_GENERATION:
				Image pickupImage = new Image("images/rewind.png");
				pickupImage.setSize("15px", "15px");
				pickupImage.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						matrixGenerationService.getMatrixGenerationConfiguration(Authentication.getInstance().getAuthenticationToken(), 
								task, new AsyncCallback<MatrixGenerationConfiguration>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(MatrixGenerationConfiguration matrixGenerationConfiguration) {
										//could also have a popup here asking for a new task name to use..										
										
										//pickup again from review
										matrixGenerationService.goToTaskStage(Authentication.getInstance().getAuthenticationToken(), matrixGenerationConfiguration, 
												TaskStageEnum.REVIEW_TERMS ,new AsyncCallback<MatrixGenerationConfiguration>() {
											@Override
											public void onFailure(Throwable caught) {
												caught.printStackTrace();
											}
											@Override
											public void onSuccess(MatrixGenerationConfiguration matrixGenerationConfiguration) {
												eventBus.fireEvent(new MatrixGenerationEvent(matrixGenerationConfiguration));
											}
										});
									}
						});
					}
				});		
				actionsPanel.add(pickupImage);
			case TAXONOMY_COMPARISON:
				break;
			case TREE_GENERATION:
				break;
			case VISUALIZATION:
				break;
			default:
				break;
			}
		}
		
		Image cancelImage = new Image("images/revoke.jpg");
		cancelImage.setSize("15px", "15px");
		cancelImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				matrixGenerationService.cancel(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(Void result) {
						drawTable();
						//display.getYourTasksTable().remove.removeRow(row);
					}
				});
			}
		});
		actionsPanel.add(cancelImage);
		return actionsPanel;
	}
	
	private void drawTable() {
		for(int i=1; i<display.getHistoryTable().getRowCount(); i++)
			display.getHistoryTable().removeRow(i);
		for(int i=1; i<display.getYourTasksTable().getRowCount(); i++) 
			display.getYourTasksTable().removeRow(i);
		
		this.taskRowMap = new HashMap<Integer, Integer>();
		taskService.getCreatedTasks(Authentication.getInstance().getAuthenticationToken(), 
				new AsyncCallback<List<Task>>() {
					@Override
					public void onSuccess(List<Task> result) {
						DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
						
						for(int i=1; i<=result.size(); i++) { 
							final int j = i;
							final Task task = result.get(i-1);
							taskRowMap.put(task.getId(), i);

							display.getYourTasksTable().setText(i, 0, task.getName());
							display.getYourTasksTable().setText(i, 1, dateTimeFormat.format(task.getCreated()));
							display.getYourTasksTable().setText(i, 2, task.getTaskStage().getTaskType().getTaskTypeEnum().displayName());
							
							Panel statusPanel = getStatusPanel(task);
							display.getYourTasksTable().setWidget(i, 3, statusPanel);
							Panel actionsPanel = getActionsPanel(task);
							display.getYourTasksTable().setWidget(i, 4, actionsPanel);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
		
			taskService.getPastTasks(Authentication.getInstance().getAuthenticationToken(), new AsyncCallback<List<Task>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(List<Task> result) {
					DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
					
					for(int i=1; i<=result.size(); i++) { 
						final int j = i;
						final Task task = result.get(i-1);
						Image cancelImage = new Image("images/revoke.jpg");
						cancelImage.setSize("15px", "15px");
						cancelImage.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								matrixGenerationService.cancel(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(Void result) {
										drawTable();
										//display.getHistoryTable().removeRow(j);
									}
								});
							}
						});
						display.getHistoryTable().setText(i, 0, task.getName());
						display.getHistoryTable().setText(i, 1, dateTimeFormat.format(task.getCreated()));
						display.getHistoryTable().setText(i, 2, task.getTaskStage().getTaskType().getTaskTypeEnum().displayName());
						Panel actionsPanel = getActionsPanel(task);
						display.getHistoryTable().setWidget(i, 4, actionsPanel);
					}
				}
			});
	}
}
