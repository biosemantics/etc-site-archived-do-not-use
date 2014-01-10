package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class TaskManagerPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<TaskManagerPlace> {

		@Override
		public TaskManagerPlace getPlace(String token) {
			return new TaskManagerPlace();
		}

		@Override
		public String getToken(TaskManagerPlace place) {
			return "";
		}

	}

}
