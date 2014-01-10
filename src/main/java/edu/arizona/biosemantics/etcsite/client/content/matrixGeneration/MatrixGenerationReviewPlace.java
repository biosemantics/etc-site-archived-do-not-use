package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class MatrixGenerationReviewPlace extends MatrixGenerationPlace {

	public MatrixGenerationReviewPlace(Task task) {
		super(task);
		// TODO Auto-generated constructor stub
	}
	
	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationReviewPlace> {

		@Override
		public MatrixGenerationReviewPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new MatrixGenerationReviewPlace(null);
			}
			return new MatrixGenerationReviewPlace(task);
		}

		@Override
		public String getToken(MatrixGenerationReviewPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
