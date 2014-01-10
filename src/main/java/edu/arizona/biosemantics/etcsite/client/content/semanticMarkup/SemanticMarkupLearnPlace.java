package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupLearnPlace extends SemanticMarkupPlace {

	public SemanticMarkupLearnPlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupLearnPlace> {

		@Override
		public SemanticMarkupLearnPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupLearnPlace(null);
			}
			return new SemanticMarkupLearnPlace(task);
		}

		@Override
		public String getToken(SemanticMarkupLearnPlace place) {
			return "task=" + place.getTask().getId();
		}

	}
	
}
