package edu.arizona.biosemantics.etcsite.client.event;

import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent.ResumableTasksEventHandler;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class ResumableTasksEvent extends GwtEvent<ResumableTasksEventHandler> {

	public interface ResumableTasksEventHandler extends EventHandler {

		public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent);

	}
	
	private Map<Integer, Task> tasks;
	public static Type<ResumableTasksEventHandler> TYPE = new Type<ResumableTasksEventHandler>();

	public ResumableTasksEvent(Map<Integer, Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public GwtEvent.Type<ResumableTasksEventHandler> getAssociatedType() {
		return TYPE;
	}


	@Override
	protected void dispatch(ResumableTasksEventHandler handler) {
		handler.onResumableTaskEvent(this);
	}

	public Map<Integer, Task> getTasks() {
		return tasks;
	}

	public void setTasks(Map<Integer, Task> tasks) {
		this.tasks = tasks;
	}
	
	

}
