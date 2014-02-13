package edu.arizona.biosemantics.etcsite.client.content.visualization;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;

public class VisualizationPlace extends Place implements MenuPlace {

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
