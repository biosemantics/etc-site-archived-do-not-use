package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonViewPlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonViewPlace(Task task) {
		super(task);
		// TODO Auto-generated constructor stub
	}
	
	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonViewPlace> {

		@Override
		public TaxonomyComparisonViewPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonViewPlace(null);
			}
			return new TaxonomyComparisonViewPlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonViewPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
