package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupToOntologiesPlace extends SemanticMarkupPlace {

	public SemanticMarkupToOntologiesPlace() {
		super(null);
	}
	
	public SemanticMarkupToOntologiesPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupToOntologiesPlace> {

		@Override
		public SemanticMarkupToOntologiesPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupToOntologiesPlace(null);
			}
			return new SemanticMarkupToOntologiesPlace(task);
		}

		@Override
		public String getToken(SemanticMarkupToOntologiesPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
