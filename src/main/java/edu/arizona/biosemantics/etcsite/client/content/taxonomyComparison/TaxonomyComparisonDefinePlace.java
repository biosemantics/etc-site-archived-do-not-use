package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TaxonomyComparisonDefinePlace extends TaxonomyComparisonPlace {

	public TaxonomyComparisonDefinePlace() {
		super(null);
	}
	
	public TaxonomyComparisonDefinePlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<TaxonomyComparisonDefinePlace> {
		public Tokenizer(){}
		@Override
		public TaxonomyComparisonDefinePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TaxonomyComparisonDefinePlace(null);
			}
			return new TaxonomyComparisonDefinePlace(task);
		}

		@Override
		public String getToken(TaxonomyComparisonDefinePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "TaxonomyComparisonDefinePlace";
	}
}
