package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.SemanticMarkupTaskRun;

public class SemanticMarkupEvent extends GwtEvent<SemanticMarkupEventHandler> implements IETCSiteEvent, ITaskEvent<SemanticMarkupTaskRun> {

	public static Type<SemanticMarkupEventHandler> TYPE = new Type<SemanticMarkupEventHandler>();
	private SemanticMarkupTaskRun taskConfiguration;
		
	public SemanticMarkupEvent() { }
	
	public SemanticMarkupEvent(SemanticMarkupTaskRun taskConfiguration) {
		this.taskConfiguration = taskConfiguration;
	}
	
	@Override
	public SemanticMarkupTaskRun getTaskConfiguration() {
		return taskConfiguration;
	}

	@Override
	public void setTaskConfiguration(SemanticMarkupTaskRun task) {
		this.taskConfiguration = task;
	}

	@Override
	public Type<SemanticMarkupEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SemanticMarkupEventHandler handler) {
		handler.onSemanticMarkup(this);
	}

	@Override
	public boolean hasTaskConfiguration() {
		return this.taskConfiguration != null;
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.SEMANTIC_MARKUP;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
