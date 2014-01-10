package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

public class PlaceControllerProvider implements Provider<PlaceController> {

	private EventBus eventBus;
	
	@Inject
	public PlaceControllerProvider(@Named("ActivitiesBus")EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public PlaceController get() {
		PlaceController	placeController = new PlaceController(eventBus);
		return placeController;
	}

}
