package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class TaskManagerEvent extends GwtEvent<TaskManagerEventHandler> implements ETCSiteEvent {

	public static Type<TaskManagerEventHandler> TYPE = new Type<TaskManagerEventHandler>();
	
	@Override
	public Type<TaskManagerEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TaskManagerEventHandler handler) {
		handler.onTaskManager(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.TASK_MANAGER;
	}
	
	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}

}
