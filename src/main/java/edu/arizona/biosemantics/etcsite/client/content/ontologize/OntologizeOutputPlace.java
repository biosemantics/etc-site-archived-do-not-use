package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class OntologizeOutputPlace extends OntologizePlace {

	public OntologizeOutputPlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<OntologizeOutputPlace> {

		public Tokenizer(){}
		@Override
		public OntologizeOutputPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new OntologizeOutputPlace(null);
			}
			return new OntologizeOutputPlace(task);
		}

		@Override
		public String getToken(OntologizeOutputPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "OntologizeOutputPlace";
	}
}
