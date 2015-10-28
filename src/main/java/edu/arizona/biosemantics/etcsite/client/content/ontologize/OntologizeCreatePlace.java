package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class OntologizeCreatePlace extends OntologizePlace {

	public OntologizeCreatePlace() {
		super(null);
	}
	
	public OntologizeCreatePlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<OntologizeCreatePlace> {

		@Override
		public OntologizeCreatePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new OntologizeCreatePlace(null);
			}
			return new OntologizeCreatePlace(task);
		}

		@Override
		public String getToken(OntologizeCreatePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "OntologizeCreatePlace";
	}
}
