package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonPlace extends HasTaskPlace implements RequiresAuthenticationPlace  {
	
	public TaxonomyComparisonPlace(Task task) {
		super(task);
	}

	public TaxonomyComparisonPlace() {
	}


}
