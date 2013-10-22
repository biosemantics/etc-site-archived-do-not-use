package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.VisualizationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;

public class VisualizationEvent extends GwtEvent<VisualizationEventHandler> implements IETCSiteEvent, ITaskEvent<VisualizationTaskRun> {

	public static Type<VisualizationEventHandler> TYPE = new Type<VisualizationEventHandler>();
	private VisualizationTaskRun taskConfiguration;
	
	public VisualizationEvent() { }
	
	public VisualizationEvent(VisualizationTaskRun taskConfiguration) { 
		this.taskConfiguration = taskConfiguration;
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
	public VisualizationTaskRun getTaskConfiguration() {
		return taskConfiguration;
	}

	@Override
	public void setTaskConfiguration(VisualizationTaskRun task) {
		this.taskConfiguration = task;
	}

	@Override
	public boolean hasTaskConfiguration() {
		return taskConfiguration != null;
	}
}
