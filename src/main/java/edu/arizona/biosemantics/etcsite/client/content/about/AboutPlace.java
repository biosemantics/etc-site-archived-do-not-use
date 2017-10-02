package edu.arizona.biosemantics.etcsite.client.content.about;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AboutPlace extends Place {
	
	/*In /target/.generated/.../MyPlaceHistoryMapperImpl, GWT.create() is used on AboutPlace.Tokenizer*/
	/*this requires the Tokenizer to be a static class with a zero-argument constructor*/
	public static class Tokenizer implements PlaceTokenizer<AboutPlace> {
		
		public Tokenizer(){}

		@Override
		public AboutPlace getPlace(String token) {
			return new AboutPlace();
		}

		@Override
		public String getToken(AboutPlace place) {
			return "";
		}

	}
	
	@Override
	public String toString(){
		return "AboutPlace";
	}

}
