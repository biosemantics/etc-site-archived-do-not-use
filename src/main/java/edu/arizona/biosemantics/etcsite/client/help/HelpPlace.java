package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class HelpPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<HelpPlace> {
		public Tokenizer(){}
		@Override
		public HelpPlace getPlace(String token) {
			return new HelpPlace();
		}

		@Override
		public String getToken(HelpPlace place) {
			return "";
		}

	}

}
