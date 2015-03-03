package edu.arizona.biosemantics.etcsite.client.event;

import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent.FailedTasksEventHandler;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class FailedTasksEvent extends GwtEvent<FailedTasksEventHandler> {

	public interface FailedTasksEventHandler extends EventHandler {

		public void onFailedTasksEvent(FailedTasksEvent failedTasksEvent);

	}
	
	private Map<Integer, Task> tasks;
	public static Type<FailedTasksEventHandler> TYPE = new Type<FailedTasksEventHandler>();

	public FailedTasksEvent(Map<Integer, Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public GwtEvent.Type<FailedTasksEventHandler> getAssociatedType() {
		return TYPE;
	}


	@Override
	protected void dispatch(FailedTasksEventHandler handler) {
		handler.onFailedTasksEvent(this);
	}

	public Map<Integer, Task> getTasks() {
		return tasks;
	}

	public void setTasks(Map<Integer, Task> tasks) {
		this.tasks = tasks;
	}
	
	

}
