package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupReviewPlace extends SemanticMarkupPlace {

	public SemanticMarkupReviewPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupReviewPlace> {

		@Override
		public SemanticMarkupReviewPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupReviewPlace(null);
			}
			return new SemanticMarkupReviewPlace(task);
		}

		@Override
		public String getToken(SemanticMarkupReviewPlace place) {
			return "task=" + place.getTask().getId();
		}
	}
	
}
