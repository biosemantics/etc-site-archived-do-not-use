package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;

public class TreeGenerationCreatePlace extends TreeGenerationPlace {

	public TreeGenerationCreatePlace() {
		super(null);
	}
	
	public TreeGenerationCreatePlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<TreeGenerationCreatePlace> {

		@Override
		public TreeGenerationCreatePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new TreeGenerationCreatePlace(null);
			}
			return new TreeGenerationCreatePlace(task);
		}

		@Override
		public String getToken(TreeGenerationCreatePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "TreeGenerationCreatePlace";
	}

}
