package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class SemanticMarkupEvent extends GwtEvent<SemanticMarkupEventHandler> implements IETCSiteEvent, ITaskEvent {

	public static Type<SemanticMarkupEventHandler> TYPE = new Type<SemanticMarkupEventHandler>();
	private Task task;
		
	public SemanticMarkupEvent() { }
	
	public SemanticMarkupEvent(Task task) {
		this.task = task;
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
	public Type<SemanticMarkupEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SemanticMarkupEventHandler handler) {
		handler.onSemanticMarkup(this);
	}

	@Override
	public boolean hasTask() {
		return this.task != null;
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
