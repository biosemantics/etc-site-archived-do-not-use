package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class NewsPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<NewsPlace> {

		public Tokenizer(){}
		
		@Override
		public NewsPlace getPlace(String token) {
			return new NewsPlace();
		}

		@Override
		public String getToken(NewsPlace place) {
			return "";
		}

	}
	@Override
	public String toString(){
		return "NewsPlace";
	}
}
