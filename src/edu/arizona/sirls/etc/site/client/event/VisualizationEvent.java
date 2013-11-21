package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class VisualizationEvent extends GwtEvent<VisualizationEventHandler> implements IETCSiteEvent, ITaskEvent {

	public static Type<VisualizationEventHandler> TYPE = new Type<VisualizationEventHandler>();
	private Task task;
	
	public VisualizationEvent() { }
	
	public VisualizationEvent(Task task) { 
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

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.VISUALIZATION;
	}
	
	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}

	@Override
	public Task getTask() {
		return task;
	}

	@Override
	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public boolean hasTask() {
		return task != null;
	}
}
