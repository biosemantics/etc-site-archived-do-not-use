package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;

public class SettingsPlace extends Place implements RequiresAuthenticationPlace  {

	private String action;
	private String accessToken;

	public SettingsPlace() {
	}
	
	public SettingsPlace(String action, String accessToken) {
		this.action = action;
		this.accessToken = accessToken;
	}
	
	

	public String getAction() {
		return action;
	}

	public String getAccessToken() {
		return accessToken;
	}



	public static class Tokenizer implements PlaceTokenizer<SettingsPlace>{
		public Tokenizer(){}
		@Override
		public SettingsPlace getPlace(String token) {
			return new SettingsPlace();
		}

		@Override
		public String getToken(SettingsPlace place) {
			return "";
		}

	}
	@Override
	public String toString(){
		return "SettingsPlace";
	}
}
