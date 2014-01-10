package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class MatrixGenerationProcessPlace extends MatrixGenerationPlace {

	public MatrixGenerationProcessPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationProcessPlace> {

		@Override
		public MatrixGenerationProcessPlace getPlace(String token) {
			Task task = new Task();
			try {
				int taskId = Integer.parseInt(token.split("task=")[1]);
				task.setId(taskId);
			} catch(Exception e) {
				return new MatrixGenerationProcessPlace(null);
			}
			return new MatrixGenerationProcessPlace(task);
		}

		@Override
		public String getToken(MatrixGenerationProcessPlace place) {
			return "task=" + place.getTask().getId();
		}

	}

}
