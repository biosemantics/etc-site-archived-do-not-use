package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

public class PlaceHistoryHandlerProvider implements Provider<PlaceHistoryHandler> {

	private PlaceHistoryMapper placeHistoryMapper;
	private PlaceController placeController;
	private EventBus eventBus;
	private Place defaultPlace;

	@Inject
	public PlaceHistoryHandlerProvider(PlaceHistoryMapper placeHistoryMapper, 
			PlaceController placeController, @Named("ActivitiesBus")EventBus eventBus, 
			@Named("DefaultPlace")Place defaultPlace) {
		this.placeHistoryMapper = placeHistoryMapper;
		this.placeController = placeController;
		this.eventBus = eventBus;
		this.defaultPlace = defaultPlace;
	}
	
	@Override
	public PlaceHistoryHandler get() {
		PlaceHistoryHandler	placeHistoryHandler = new PlaceHistoryHandler(placeHistoryMapper);
		placeHistoryHandler.register(placeController, eventBus, defaultPlace);
		return placeHistoryHandler;
	}

}
