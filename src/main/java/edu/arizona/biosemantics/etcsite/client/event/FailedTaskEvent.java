package edu.arizona.biosemantics.etcsite.client.event;

import java.util.Date;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.client.event.FailedTaskEvent.FailedTaskEventHandler;

public class FailedTaskEvent extends GwtEvent<FailedTaskEventHandler> {

	public interface FailedTaskEventHandler extends EventHandler {

		public void onResumableTaskEvent(FailedTaskEvent failedTaskEvent);

	}
	
	private Task task;
	public static Type<FailedTaskEventHandler> TYPE = new Type<FailedTaskEventHandler>();

	public FailedTaskEvent(Task task) {
		this.task = task;
		task.setFailed(true);
		task.setFailedTime(new Date());
	}

	@Override
	public GwtEvent.Type<FailedTaskEventHandler> getAssociatedType() {
		return TYPE;
	}


	@Override
	protected void dispatch(FailedTaskEventHandler handler) {
		handler.onResumableTaskEvent(this);
	}
	
	public Task getTask() {
		return task;
	}
	
	

}
