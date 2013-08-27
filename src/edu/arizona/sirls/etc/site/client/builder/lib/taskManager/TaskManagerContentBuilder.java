package edu.arizona.sirls.etc.site.client.builder.lib.taskManager;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.Task;

public class TaskManagerContentBuilder implements IContentBuilder {
	
	private final ITaskServiceAsync taskService = GWT.create(ITaskService.class);
	private FlexTable yourTasksTable;
	private FlexTable sharedTasksTable;

	@Override
	public void build() {
		createHTML();
		createWidgets();
	}

	private void createWidgets() {
		this.yourTasksTable = new FlexTable();
		yourTasksTable.setStyleName("table");
		DOM.setElementAttribute(yourTasksTable.getElement(), "id", "yourTasksTable");
		yourTasksTable.setTitle("Your tasks");
		Label startedLabel = new Label("Started");
		startedLabel.setStyleName("tableHeader");
		Label taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		Label taskName = new Label("Name");
		taskName.setStyleName("tableHeader");
		Label progressLabel = new Label("Progress");
		progressLabel.setStyleName("tableHeader");
		
		yourTasksTable.setWidget(0,  0, startedLabel);
		yourTasksTable.setWidget(0,  1, taskTypeLabel);
		yourTasksTable.setWidget(0,  2, taskName);
		yourTasksTable.setWidget(0,  3, progressLabel);
		
		taskService.getCreatedTasks(Authentication.getInstance().getAuthenticationToken(), createdTaskCallback);
		
		this.sharedTasksTable = new FlexTable();
		sharedTasksTable.setStyleName("table");
		DOM.setElementAttribute(sharedTasksTable.getElement(), "id", "sharedTasksTable");
		sharedTasksTable.setTitle("Shared tasks");
		startedLabel = new Label("Started");
		startedLabel.setStyleName("tableHeader");
		taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		taskName = new Label("Name");
		taskName.setStyleName("tableHeader");
		progressLabel = new Label("Progress");
		progressLabel.setStyleName("tableHeader");
		
		sharedTasksTable.setWidget(0,  0, startedLabel);
		sharedTasksTable.setWidget(0,  1, taskTypeLabel);
		sharedTasksTable.setWidget(0,  2, taskName);
		sharedTasksTable.setWidget(0,  3, progressLabel);

		taskService.getSharedTasks(Authentication.getInstance().getAuthenticationToken(), sharedTaskCallback);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(yourTasksTable);
		horizontalPanel.add(sharedTasksTable);

	    RootPanel.get("taskManagerContent").add(horizontalPanel);
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'><div id='taskManagerContent'></div></div>");
	}
	
	protected AsyncCallback<List<Task>> sharedTaskCallback = new AsyncCallback<List<Task>>() {
		public void onSuccess(List<Task> result) {
			DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
			
			for(int i=1; i<=result.size(); i++) { 
				Task task = result.get(i-1);
				sharedTasksTable.setText(i, 0, dateTimeFormat.format(task.getStart()));
				sharedTasksTable.setText(i, 1, task.getTaskType().toString());
				sharedTasksTable.setText(i, 2, task.getName());
				sharedTasksTable.setText(i, 3, task.getProgress() + "%");
			}
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
	
	protected AsyncCallback<List<Task>> createdTaskCallback = new AsyncCallback<List<Task>>() {
		public void onSuccess(List<Task> result) {
			DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
			
			for(int i=1; i<=result.size(); i++) { 
				Task task = result.get(i-1);
				yourTasksTable.setText(i, 0, dateTimeFormat.format(task.getStart()));
				yourTasksTable.setText(i, 1, task.getTaskType().toString());
				yourTasksTable.setText(i, 2, task.getName());
				yourTasksTable.setText(i, 3, task.getProgress() + "%");
			}
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};

}
