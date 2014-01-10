package edu.arizona.biosemantics.etcsite.client.content.visualization;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class VisualizationPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<VisualizationPlace> {

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
