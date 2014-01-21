package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class SemanticMarkupHierarchyPlace extends SemanticMarkupPlace {

	public SemanticMarkupHierarchyPlace() {
		super(null);
	}
	
	public SemanticMarkupHierarchyPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupHierarchyPlace> {

		@Override
		public SemanticMarkupHierarchyPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupHierarchyPlace(null);
			}
			return new SemanticMarkupHierarchyPlace(task);
		}

		@Override
		public String getToken(SemanticMarkupHierarchyPlace place) {
			return "task=" + place.getTask().getId();
		}

	}
}
