package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class VisualizationEvent extends GwtEvent<VisualizationEventHandler> {

	public static Type<VisualizationEventHandler> TYPE = new Type<VisualizationEventHandler>();
	
	private Task task;
	
	public VisualizationEvent(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public Type<VisualizationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(VisualizationEventHandler handler) {
		handler.onVisualization(this);
	}
}
