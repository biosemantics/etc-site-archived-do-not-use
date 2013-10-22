package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.TaxonomyComparisonTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonEvent extends GwtEvent<TaxonomyComparisonEventHandler> implements IETCSiteEvent, ITaskEvent<TaxonomyComparisonTaskRun> {

	public static Type<TaxonomyComparisonEventHandler> TYPE = new Type<TaxonomyComparisonEventHandler>();

	private TaxonomyComparisonTaskRun taskConfiguration;
	
	public TaxonomyComparisonEvent() { }
	
	public TaxonomyComparisonEvent(TaxonomyComparisonTaskRun taskConfiguration) {
		this.taskConfiguration = taskConfiguration;
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
	public TaxonomyComparisonTaskRun getTaskConfiguration() {
		return taskConfiguration;
	}

	@Override
	public void setTaskConfiguration(TaxonomyComparisonTaskRun taskConfiguration) {
		this.taskConfiguration = taskConfiguration;
	}

	@Override
	public boolean hasTaskConfiguration() {
		return taskConfiguration != null;
	}
}
