package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonEvent extends GwtEvent<TaxonomyComparisonEventHandler> {

	public static Type<TaxonomyComparisonEventHandler> TYPE = new Type<TaxonomyComparisonEventHandler>();
	
	private TaxonomyComparisonConfiguration taxonomyComparisonConfiguration;
	
	public TaxonomyComparisonEvent() { }
	
	public TaxonomyComparisonEvent(TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		this.taxonomyComparisonConfiguration = taxonomyComparisonConfiguration;
	}
	public TaxonomyComparisonConfiguration getTaxonomyComparisonConfiguration() {
		return taxonomyComparisonConfiguration;
	}

	public void setTaxonomyComparisonConfiguration(TaxonomyComparisonConfiguration taxonomyComparisonConfiguration) {
		this.taxonomyComparisonConfiguration = taxonomyComparisonConfiguration;
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
