package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonAlignPlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonAlignPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonAlignPlace> {
		public Tokenizer(){}
		@Override
		public TaxonomyComparisonAlignPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonAlignPlace(null);
			}
			return new TaxonomyComparisonAlignPlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonAlignPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "TaxonomyComparisonAlignPlace";
	}

}
