package edu.arizona.sirls.etc.site.client.presenter;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.Task;

public class TaskManagerPresenter {
	
	public interface Display {
		Widget asWidget();
		FlexTable getSharedTasksTable();
		FlexTable getYourTasksTable();
	}

	private HandlerManager eventBus;
	private Display display;
	private ITaskServiceAsync taskService;

	public TaskManagerPresenter(HandlerManager eventBus,
			Display display, ITaskServiceAsync taskService) {
		this.eventBus = eventBus;
		this.display = display;
		this.taskService = taskService;
	}

	public void go(HasWidgets content) {
		taskService.getCreatedTasks(Authentication.getInstance().getAuthenticationToken(), 
			new AsyncCallback<List<Task>>() {
				@Override
				public void onSuccess(List<Task> result) {
					DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy HH:mm");
					
					for(int i=1; i<=result.size(); i++) { 
						Task task = result.get(i-1);
						display.getSharedTasksTable().setText(i, 0, dateTimeFormat.format(task.getStart()));
						display.getSharedTasksTable().setText(i, 1, task.getTaskType().toString());
						display.getSharedTasksTable().setText(i, 2, task.getName());
						display.getSharedTasksTable().setText(i, 3, task.getProgress() + "%");
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
						Task task = result.get(i-1);
						display.getYourTasksTable().setText(i, 0, dateTimeFormat.format(task.getStart()));
						display.getYourTasksTable().setText(i, 1, task.getTaskType().toString());
						display.getYourTasksTable().setText(i, 2, task.getName());
						display.getYourTasksTable().setText(i, 3, task.getProgress() + "%");
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
