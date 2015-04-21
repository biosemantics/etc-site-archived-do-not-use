package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.help.HelpSemanticMarkupPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class SemanticMarkupCreatePlace extends SemanticMarkupPlace implements HelpSemanticMarkupPlace{

	public SemanticMarkupCreatePlace() {
		super(null);
	}
	
	public SemanticMarkupCreatePlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<SemanticMarkupCreatePlace> {

		@Override
		public SemanticMarkupCreatePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new SemanticMarkupCreatePlace(null);
			}
			return new SemanticMarkupCreatePlace(task);
		}

		@Override
		public String getToken(SemanticMarkupCreatePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	
}
