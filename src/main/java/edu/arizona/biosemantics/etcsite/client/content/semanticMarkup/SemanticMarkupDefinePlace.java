package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupDefinePlace extends SemanticMarkupPlace{
	
	public SemanticMarkupDefinePlace() {
		super(null);
	}
	
	public SemanticMarkupDefinePlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupDefinePlace> {

		public Tokenizer(){}
		@Override
		public SemanticMarkupDefinePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupDefinePlace(null);
			}
			return new SemanticMarkupDefinePlace(task);
		}

		@Override
		public String getToken(SemanticMarkupDefinePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "SemanticMarkupDefinePlace";
	}	
}
