package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class TreeGenerationEvent extends GwtEvent<TreeGenerationEventHandler> {

	public static Type<TreeGenerationEventHandler> TYPE = new Type<TreeGenerationEventHandler>();
	private Task task;
	
	public TreeGenerationEvent(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
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

}
