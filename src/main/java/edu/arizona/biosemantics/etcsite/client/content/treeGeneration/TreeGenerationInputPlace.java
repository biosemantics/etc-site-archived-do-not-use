package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TreeGenerationInputPlace extends TreeGenerationPlace {

	public TreeGenerationInputPlace() {
		super(null);
	}
	
	public TreeGenerationInputPlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<TreeGenerationInputPlace> {
		public Tokenizer(){}
		@Override
		public TreeGenerationInputPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TreeGenerationInputPlace(null);
			}
			return new TreeGenerationInputPlace(task);
		}

		@Override
		public String getToken(TreeGenerationInputPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "TreeGenerationInputPlace";
	}

}
