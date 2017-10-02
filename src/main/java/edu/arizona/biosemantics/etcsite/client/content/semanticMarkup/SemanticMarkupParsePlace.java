package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupParsePlace extends SemanticMarkupPlace {

	public SemanticMarkupParsePlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupParsePlace> {
		public Tokenizer(){}
		@Override
		public SemanticMarkupParsePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupParsePlace(null);
			}
			return new SemanticMarkupParsePlace(task);
		}

		@Override
		public String getToken(SemanticMarkupParsePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}
	}
	@Override
	public String toString(){
		return "SemanticMarkupParsePlace";
	}
}
