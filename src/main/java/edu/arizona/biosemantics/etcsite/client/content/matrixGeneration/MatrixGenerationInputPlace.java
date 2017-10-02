package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class MatrixGenerationInputPlace extends MatrixGenerationPlace{

	public MatrixGenerationInputPlace() {
		super(null);
	}
	
	public MatrixGenerationInputPlace(Task task) {
		super(task);
	}

	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationInputPlace> {

		public Tokenizer(){}
		
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
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}
	
	@Override
	public String toString(){
		return "MatrixGenerationInputPlace";
	}
}
