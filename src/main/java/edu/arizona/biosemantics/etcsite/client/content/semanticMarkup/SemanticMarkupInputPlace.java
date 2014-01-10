package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupInputPlace extends SemanticMarkupPlace {
	
	public SemanticMarkupInputPlace() {
		super(null);
	}
	
	public SemanticMarkupInputPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupInputPlace> {

		@Override
		public SemanticMarkupInputPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupInputPlace(null);
			}
			return new SemanticMarkupInputPlace(task);
		}

		@Override
		public String getToken(SemanticMarkupInputPlace place) {
			return "task=" + place.getTask().getId();
		}

	}
	
}
