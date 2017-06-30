package edu.arizona.biosemantics.etcsite.client.content.visualization;

import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class VisualizationPlace extends HasTaskPlace  {

	public VisualizationPlace() {
		super(null);
	}
	
	public VisualizationPlace(Task task) {
		super(task);
	}
	
	public static class Tokenizer implements PlaceTokenizer<VisualizationPlace> {
		public Tokenizer(){}
		@Override
		public VisualizationPlace getPlace(String token) {
			return new VisualizationPlace();
		}

		@Override
		public String getToken(VisualizationPlace place) {
			return "";
		}

	}

}
