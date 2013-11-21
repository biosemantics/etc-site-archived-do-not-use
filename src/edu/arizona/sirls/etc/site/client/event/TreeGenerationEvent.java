package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class TreeGenerationEvent extends GwtEvent<TreeGenerationEventHandler> implements IETCSiteEvent, ITaskEvent {

	public static Type<TreeGenerationEventHandler> TYPE = new Type<TreeGenerationEventHandler>();
	private Task task;
	
	public TreeGenerationEvent() { }
	
	public TreeGenerationEvent(Task taskConfiguration) { 
		this.task = task;
	}
	
	@Override
	public Type<TreeGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TreeGenerationEventHandler handler) {
		handler.onTreeGeneration(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.TREE_GENERATION;
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
