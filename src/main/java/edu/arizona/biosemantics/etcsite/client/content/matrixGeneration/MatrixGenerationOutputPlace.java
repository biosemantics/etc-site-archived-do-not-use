package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class MatrixGenerationOutputPlace extends MatrixGenerationPlace {

	public MatrixGenerationOutputPlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationOutputPlace> {

		public Tokenizer(){}
		
		@Override
		public MatrixGenerationOutputPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new MatrixGenerationOutputPlace(null);
			}
			return new MatrixGenerationOutputPlace(task);
		}

		@Override
		public String getToken(MatrixGenerationOutputPlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	@Override
	public String toString(){
		return "MatrixGenerationOutputPlace";
	}
}
