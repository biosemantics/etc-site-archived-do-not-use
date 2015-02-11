package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonReviewPlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonReviewPlace(Task task) {
		super(task);
		// TODO Auto-generated constructor stub
	}
	
	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonReviewPlace> {

		@Override
		public TaxonomyComparisonReviewPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonReviewPlace(null);
			}
			return new TaxonomyComparisonReviewPlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonReviewPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
