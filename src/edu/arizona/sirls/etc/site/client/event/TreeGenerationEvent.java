package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.TreeGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;

public class TreeGenerationEvent extends GwtEvent<TreeGenerationEventHandler> implements IETCSiteEvent, ITaskEvent<TreeGenerationTaskRun> {

	public static Type<TreeGenerationEventHandler> TYPE = new Type<TreeGenerationEventHandler>();
	private TreeGenerationTaskRun taskConfiguration;
	
	public TreeGenerationEvent() { }
	
	public TreeGenerationEvent(TreeGenerationTaskRun taskConfiguration) { 
		this.taskConfiguration = taskConfiguration;
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
	public TreeGenerationTaskRun getTaskConfiguration() {
		return taskConfiguration;
	}

	@Override
	public void setTaskConfiguration(TreeGenerationTaskRun task) {
		this.taskConfiguration = task;
	}

	@Override
	public boolean hasTaskConfiguration() {
		return taskConfiguration != null;
	}
}
