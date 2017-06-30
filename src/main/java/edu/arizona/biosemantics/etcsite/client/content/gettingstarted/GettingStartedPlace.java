package edu.arizona.biosemantics.etcsite.client.content.gettingstarted;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class GettingStartedPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<GettingStartedPlace> {

		public Tokenizer(){}
		
		@Override
		public GettingStartedPlace getPlace(String token) {
			return new GettingStartedPlace();
		}

		@Override
		public String getToken(GettingStartedPlace place) {
			return "";
		}

	}
	
	@Override
	public String toString(){
		return "GettingStartedPlace";
	}
	
}
