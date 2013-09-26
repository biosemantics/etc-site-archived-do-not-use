package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;

public class TreeGenerationEvent extends GwtEvent<TreeGenerationEventHandler> implements ETCSiteEvent {

	public static Type<TreeGenerationEventHandler> TYPE = new Type<TreeGenerationEventHandler>();
	private TreeGenerationConfiguration treeGenerationConfiguration;
	
	public TreeGenerationEvent() { }
	
	public TreeGenerationEvent(TreeGenerationConfiguration treeGenerationConfiguration) {
		this.treeGenerationConfiguration = treeGenerationConfiguration;
	}
	
	public TreeGenerationConfiguration getTreeGenerationConfiguration() {
		return treeGenerationConfiguration;
	}

	public void setTreeGenerationConfiguration(TreeGenerationConfiguration treeGenerationConfiguration) {
		this.treeGenerationConfiguration = treeGenerationConfiguration;
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
}
