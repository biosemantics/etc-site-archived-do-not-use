package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class HomePlace extends Place{

	public static class Tokenizer implements PlaceTokenizer<HomePlace> {

		public Tokenizer(){}
		
		@Override
		public HomePlace getPlace(String token) {
			return new HomePlace();
		}

		@Override
		public String getToken(HomePlace place) {
			return "";
		}

	}
	
	@Override
	public String toString(){
		return "HomePlace";
	}
}
