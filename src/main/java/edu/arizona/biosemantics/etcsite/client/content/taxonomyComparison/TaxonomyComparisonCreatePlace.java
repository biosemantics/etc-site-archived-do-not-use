package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonCreatePlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonCreatePlace() {
		super(null);
	}
	
	public TaxonomyComparisonCreatePlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonCreatePlace> {

		@Override
		public TaxonomyComparisonCreatePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonCreatePlace(null);
			}
			return new TaxonomyComparisonCreatePlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonCreatePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}

}
