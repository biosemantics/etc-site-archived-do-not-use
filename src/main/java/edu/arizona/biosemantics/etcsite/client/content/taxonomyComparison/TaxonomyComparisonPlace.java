package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class TaxonomyComparisonPlace extends HasTaskPlace implements MenuPlace {

	public TaxonomyComparisonPlace() {
		super(null);
	}
	
	public TaxonomyComparisonPlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonPlace> {

		@Override
		public TaxonomyComparisonPlace getPlace(String token) {
			return new TaxonomyComparisonPlace();
		}

		@Override
		public String getToken(TaxonomyComparisonPlace place) {
			return "";
		}

	}

}
