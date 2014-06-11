package edu.arizona.biosemantics.etcsite.client.content.help;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.menu.StartMenuPlace;

public class HelpPlace extends Place implements StartMenuPlace {

	public static class Tokenizer implements PlaceTokenizer<HelpPlace> {

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
