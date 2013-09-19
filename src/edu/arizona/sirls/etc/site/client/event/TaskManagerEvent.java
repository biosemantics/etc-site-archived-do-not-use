package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TaskManagerEvent extends GwtEvent<TaskManagerEventHandler> {

	public static Type<TaskManagerEventHandler> TYPE = new Type<TaskManagerEventHandler>();
	
	@Override
	public Type<TaskManagerEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TaskManagerEventHandler handler) {
		handler.onTaskManager(this);
	}

}
