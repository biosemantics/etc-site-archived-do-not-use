package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonEvent extends GwtEvent<TaxonomyComparisonEventHandler> implements IETCSiteEvent, ITaskEvent {

	public static Type<TaxonomyComparisonEventHandler> TYPE = new Type<TaxonomyComparisonEventHandler>();

	private Task task;
	
	public TaxonomyComparisonEvent() { }
	
	public TaxonomyComparisonEvent(Task task) {
		this.task = task;
	}

	@Override
	public Type<TaxonomyComparisonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TaxonomyComparisonEventHandler handler) {
		handler.onTaxonomyComparison(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.TAXONOMY_COMPARISON;
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
