package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;

public class MatrixGenerationCreatePlace extends MatrixGenerationPlace{

	public MatrixGenerationCreatePlace() {
		super(null);
	}
	
	public MatrixGenerationCreatePlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationCreatePlace> {

		@Override
		public MatrixGenerationCreatePlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new MatrixGenerationCreatePlace(null);
			}
			return new MatrixGenerationCreatePlace(task);
		}

		@Override
		public String getToken(MatrixGenerationCreatePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	
	@Override
	public String toString(){
		return "MatrixGenerationCreatePlace";
	}
}
