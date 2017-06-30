package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupPreprocessPlace extends SemanticMarkupPlace {

	public SemanticMarkupPreprocessPlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupPreprocessPlace> {
		public Tokenizer(){}		
		@Override
		public SemanticMarkupPreprocessPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupPreprocessPlace(null);
			}
			return new SemanticMarkupPreprocessPlace(task);
		}

		@Override
		public String getToken(SemanticMarkupPreprocessPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "SemanticMarkupPreprocessPlace";
	}
	
}
