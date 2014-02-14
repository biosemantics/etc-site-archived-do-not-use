package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;
import edu.arizona.biosemantics.etcsite.client.menu.StartMenuPlace;

public class FileManagerPlace extends Place  implements MenuPlace {
	
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
