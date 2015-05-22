package edu.arizona.biosemantics.etcsite.client.common;

import java.util.LinkedList;

import com.google.gwt.place.shared.Place;

public interface ToPlaceGoer {

	public void goTo(Place newPlace, LinkedList<ToPlaceGoer> nextToPlaceGoers);
	
}
