package edu.arizona.biosemantics.etcsite.client.content.sample;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;

public class SamplePlace extends Place implements RequiresAuthenticationPlace  {

	public SamplePlace() {
	}
	
	public static class Tokenizer implements PlaceTokenizer<SamplePlace>{
		
		public Tokenizer(){}
		
		@Override
		public SamplePlace getPlace(String token) {
			return new SamplePlace();
		}

		@Override
		public String getToken(SamplePlace place) {
			return "";
		}

	}

}
