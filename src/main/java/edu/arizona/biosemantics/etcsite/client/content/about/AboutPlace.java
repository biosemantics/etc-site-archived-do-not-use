package edu.arizona.biosemantics.etcsite.client.content.about;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.menu.StartMenuPlace;

public class AboutPlace extends Place implements StartMenuPlace{

	public static class Tokenizer implements PlaceTokenizer<AboutPlace> {

		@Override
		public AboutPlace getPlace(String token) {
			return new AboutPlace();
		}

		@Override
		public String getToken(AboutPlace place) {
			return "";
		}

	}

}
