package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class TaxonomyComparisonEvent extends GwtEvent<TaxonomyComparisonEventHandler> {

	public static Type<TaxonomyComparisonEventHandler> TYPE = new Type<TaxonomyComparisonEventHandler>();
	
	private Task task;
	
	public TaxonomyComparisonEvent(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
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

}
