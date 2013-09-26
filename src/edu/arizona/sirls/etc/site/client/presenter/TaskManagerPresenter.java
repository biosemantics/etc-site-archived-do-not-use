package edu.arizona.sirls.etc.site.client.presenter;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
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

public class TaskManagerPresenter {
	
	public interface Display {
		Widget asWidget();
		FlexTable getSharedTasksTable();
		FlexTable getYourTasksTable();
	}

	private HandlerManager eventBus;
	private Display display;
	private ITaskServiceAsync taskService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private IVisualizationServiceAsync visualizationService;

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
		taskService.getCreatedTasks(Authentication.getInstance().getAuthenticationToken(), 
			new AsyncCallback<List<Task>>() {
				@Override
				public void onSuccess(List<Task> result) {
					DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
					
					for(int i=1; i<=result.size(); i++) { 
						final Task task = result.get(i-1);
						Image image = new Image("images/Failure.gif");
						if(task.isResumable()) {
							image = new Image("images/Success.gif");
							image.addClickHandler(new ClickHandler() {
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
						}
						
						Date date = new Date();
						date.setTime(task.getTime());
						DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
						display.getYourTasksTable().setText(i, 0, formatter.format(date));
						display.getYourTasksTable().setText(i, 1, task.getTaskStage().getTaskType().getTaskTypeEnum().displayName());
						display.getYourTasksTable().setText(i, 2, task.getTaskStage().getName());
						display.getYourTasksTable().setText(i, 3, task.getName());
						display.getYourTasksTable().setWidget(i, 4, image);
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		taskService.getSharedTasks(Authentication.getInstance().getAuthenticationToken(), 
			new AsyncCallback<List<Task>>() {
				public void onSuccess(List<Task> result) {
					DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
					
					for(int i=1; i<=result.size(); i++) { 
						final Task task = result.get(i-1);
						Image image = new Image("images/Failure.gif");
						if(task.isResumable()) {
							image = new Image("images/Success.gif");
							image.addClickHandler(new ClickHandler() {
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
						}
						Date date = new Date();
						date.setTime(task.getTime());
						DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
						display.getYourTasksTable().setText(i, 0, formatter.format(date));
						display.getSharedTasksTable().setText(i, 1, task.getTaskStage().getTaskType().getTaskTypeEnum().displayName());
						display.getSharedTasksTable().setText(i, 2, task.getTaskStage().getName());
						display.getSharedTasksTable().setText(i, 3, task.getName());
						display.getSharedTasksTable().setWidget(i, 4, image);
					}
				}
	
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		
		content.clear();
		content.add(display.asWidget());
	}
}
