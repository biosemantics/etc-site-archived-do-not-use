package edu.arizona.sirls.etc.site.client.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class ResumableTasksEvent extends GwtEvent<ResumableTasksEventHandler> {

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
