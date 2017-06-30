package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonInputPlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonInputPlace() {
		super(null);
	}
	
	public TaxonomyComparisonInputPlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonInputPlace> {
		public Tokenizer(){}
		@Override
		public TaxonomyComparisonInputPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonInputPlace(null);
			}
			return new TaxonomyComparisonInputPlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonInputPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "TaxonomyComparisonInputPlace";
	}
}
