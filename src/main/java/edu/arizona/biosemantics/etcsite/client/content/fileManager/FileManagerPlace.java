package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class FileManagerPlace extends Place {
	
	public static class Tokenizer implements PlaceTokenizer<FileManagerPlace> {

		@Override
		public FileManagerPlace getPlace(String token) {
			return new FileManagerPlace();
		}

		@Override
		public String getToken(FileManagerPlace place) {
			return "";
		}

	}
}
