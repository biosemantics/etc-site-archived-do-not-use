package edu.arizona.biosemantics.etcsite.client.common;

import java.util.LinkedList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

public class AuthenticationToPlaceGoer implements ToPlaceGoer {

	private PlaceController placeController;
	private AuthenticationPresenter authenticationPresenter;

	@Inject
	public AuthenticationToPlaceGoer(PlaceController placeController, AuthenticationPresenter authenticationPresenter) {
		this.placeController = placeController;
		this.authenticationPresenter = authenticationPresenter;
	}
	
	@Override
	public void goTo(final Place newPlace, final LinkedList<ToPlaceGoer> nextToPlaceGoers) {
		authenticationPresenter.requireLogin(new AuthenticationPresenter.LoggedInListener() {
			@Override
			public void onLoggedIn() {
				doGoTo(newPlace, nextToPlaceGoers);
			}

			@Override
			public void onCancel() { }

		});
	}
	
	private void doGoTo(Place newPlace, LinkedList<ToPlaceGoer> nextToPlaceGoers) {
		if(nextToPlaceGoers == null || nextToPlaceGoers.isEmpty())
			placeController.goTo(newPlace);
		else {
			nextToPlaceGoers.removeFirst().goTo(newPlace, nextToPlaceGoers);
		}
	}

}
