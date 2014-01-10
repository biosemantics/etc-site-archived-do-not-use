package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class MatrixGenerationInputPlace extends MatrixGenerationPlace {

	public MatrixGenerationInputPlace() {
		super(null);
	}
	
	public MatrixGenerationInputPlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationInputPlace> {

		@Override
		public MatrixGenerationInputPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new MatrixGenerationInputPlace(null);
			}
			return new MatrixGenerationInputPlace(task);
		}

		@Override
		public String getToken(MatrixGenerationInputPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
