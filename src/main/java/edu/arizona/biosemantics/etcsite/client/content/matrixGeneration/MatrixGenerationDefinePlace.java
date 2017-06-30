package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class MatrixGenerationDefinePlace extends MatrixGenerationPlace {

	public MatrixGenerationDefinePlace() {
		super(null);
	}
	
	public MatrixGenerationDefinePlace(Task task) {
		super(task);
	}
	
	//needed becuase per default the name is used, which causes a name clash with 
	//InputPlace of semanticMarkup's
	public static class Tokenizer implements PlaceTokenizer<MatrixGenerationDefinePlace> {

		public Tokenizer(){}
		@Override
		public MatrixGenerationDefinePlace getPlace(String token) {
			Map<String, String> keyValues = new HashMap<String, String>();
			String parts[] = token.split("&");
			for(String part : parts) {
				String[] keyValue = part.split("=");
				if(keyValue.length == 2) {
					keyValues.put(keyValue[0], keyValue[1]);
				}
			}
			
			Task task = new Task();
			MatrixGenerationConfiguration config = new MatrixGenerationConfiguration();
			if(keyValues.containsKey("input")) {
				config.setInput(keyValues.get("input"));
				task.setTaskConfiguration(config);
			}
			if(keyValues.containsKey("task")) {
				try {
					int taskId = Integer.parseInt(keyValues.get("task"));
					task.setId(taskId);
				} catch(Exception e) {
					return new MatrixGenerationDefinePlace(task);
				}
				return new MatrixGenerationDefinePlace(task);
			}
			return new MatrixGenerationDefinePlace(task);
		}

		@Override
		public String getToken(MatrixGenerationDefinePlace place) {
			if(place.hasTask())
				return "task=" + place.getTask().getId();
			return "";
		}

	}

	@Override
	public String toString(){
		return "MatrixGenerationDefinePlace";
	}
}
