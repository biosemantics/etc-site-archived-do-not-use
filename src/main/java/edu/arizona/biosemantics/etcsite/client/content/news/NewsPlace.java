package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.content.help.HelpPlace;
import edu.arizona.biosemantics.etcsite.client.menu.StartMenuPlace;

public class NewsPlace extends Place implements StartMenuPlace{

	public static class Tokenizer implements PlaceTokenizer<NewsPlace> {

		@Override
		public NewsPlace getPlace(String token) {
			return new NewsPlace();
		}

		@Override
		public String getToken(NewsPlace place) {
			return "";
		}

	}

}
