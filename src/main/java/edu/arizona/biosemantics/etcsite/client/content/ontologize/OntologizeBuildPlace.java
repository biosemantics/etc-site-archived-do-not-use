package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class OntologizeBuildPlace extends OntologizePlace {

	public OntologizeBuildPlace() {
		super(null);
	}
	
	public OntologizeBuildPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<OntologizeBuildPlace> {

		public Tokenizer(){}
		@Override
		public OntologizeBuildPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new OntologizeBuildPlace(null);
			}
			return new OntologizeBuildPlace(task);
		}

		@Override
		public String getToken(OntologizeBuildPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "OntologizeBuildPlace";
	}
}
