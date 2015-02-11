package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonProcessPlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonProcessPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonProcessPlace> {

		@Override
		public TaxonomyComparisonProcessPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonProcessPlace(null);
			}
			return new TaxonomyComparisonProcessPlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonProcessPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
