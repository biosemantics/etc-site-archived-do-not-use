package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;

public class TaxonomyComparisonPlace extends Place implements MenuPlace {

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
