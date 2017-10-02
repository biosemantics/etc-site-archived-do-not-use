package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TreeGenerationViewPlace extends TreeGenerationPlace {

	public TreeGenerationViewPlace(Task task) {
		super(task);
		// TODO Auto-generated constructor stub
	}
	
	public static class Tokenizer implements PlaceTokenizer<TreeGenerationViewPlace> {
		public Tokenizer(){}
		@Override
		public TreeGenerationViewPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TreeGenerationViewPlace(null);
			}
			return new TreeGenerationViewPlace(task);
		}

		@Override
		public String getToken(TreeGenerationViewPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "TreeGenerationViewPlace";
	}
}
