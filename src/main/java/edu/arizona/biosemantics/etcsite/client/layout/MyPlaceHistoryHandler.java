package edu.arizona.biosemantics.etcsite.client.layout;
/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.LegacyHandlerWrapper;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent.AuthenticationEventType;
import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView.Presenter;

/**
 * Monitors {@link PlaceChangeEvent}s and
 * {@link com.google.gwt.user.client.History} events and keep them in sync.
 */
public class MyPlaceHistoryHandler extends PlaceHistoryHandler {

  /**
   * Default implementation of {@link Historian}, based on {@link History}.
   */
  public static class DefaultHistorian implements Historian {
    public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<String> valueChangeHandler) {
      return History.addValueChangeHandler(valueChangeHandler);
    }

    public String getToken() {
      return History.getToken();
    }

    public void newItem(String token, boolean issueEvent) {
      History.newItem(token, issueEvent);
    }
  }

  /**
   * Optional delegate in charge of History related events. Provides nice
   * isolation for unit testing, and allows pre- or post-processing of tokens.
   * Methods correspond to the like named methods on {@link History}.
   */
  public interface Historian {
    /**
     * Adds a {@link com.google.gwt.event.logical.shared.ValueChangeEvent}
     * handler to be informed of changes to the browser's history stack.
     * 
     * @param valueChangeHandler the handler
     * @return the registration used to remove this value change handler
     */
    com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<String> valueChangeHandler);

    /**
     * @return the current history token.
     */
    String getToken();

    /**
     * Adds a new browser history entry. Calling this method will cause
     * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
     * to be called as well.
     */
    void newItem(String token, boolean issueEvent);
  }

  private final Historian historian;

  private final PlaceHistoryMapper mapper;

  private PlaceController placeController;

  private Place defaultPlace = Place.NOWHERE;

private EventBus eventBus;


  /**
   * Create a new PlaceHistoryHandler with a {@link DefaultHistorian}. The
   * DefaultHistorian is created via a call to GWT.create(), so an alternative
   * default implementation can be provided through &lt;replace-with&gt; rules
   * in a {@code gwt.xml} file.
   * 
   * @param mapper a {@link PlaceHistoryMapper} instance
   */
  public MyPlaceHistoryHandler(PlaceHistoryMapper mapper, @Named("EtcSite") EventBus eventBus) {
    this(mapper, (Historian) GWT.create(DefaultHistorian.class), eventBus);
  }

  /**
   * Create a new PlaceHistoryHandler.
   * 
   * @param mapper a {@link PlaceHistoryMapper} instance
   * @param historian a {@link Historian} instance
 * @param etcSitePresenter 
   */
  public MyPlaceHistoryHandler(PlaceHistoryMapper mapper, Historian historian, EventBus eventBus) {
	  super(mapper);
    this.mapper = mapper;
    this.historian = historian;
    this.eventBus = eventBus;
  }

  /**
   * Handle the current history token. Typically called at application start, to
   * ensure bookmark launches work.
   */
  public void handleCurrentHistory() {
    handleHistoryToken(historian.getToken());
  }

  /**
   * Legacy method tied to the old location for {@link EventBus}.
   * 
   * @deprecated use {@link #register(PlaceController, EventBus, Place)}
   */
  @Deprecated
  public com.google.gwt.event.shared.HandlerRegistration register(PlaceController placeController,
      com.google.gwt.event.shared.EventBus eventBus, Place defaultPlace) {
    return new LegacyHandlerWrapper(register(placeController, (EventBus) eventBus, defaultPlace));
  }

  /**
   * Initialize this place history handler.
   * 
   * @return a registration object to de-register the handler
   */
  public HandlerRegistration register(PlaceController placeController, EventBus eventBus,
      Place defaultPlace) {
    this.placeController = placeController;
    this.defaultPlace = defaultPlace;

    final HandlerRegistration placeReg =
        eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
          public void onPlaceChange(PlaceChangeEvent event) {
            Place newPlace = event.getNewPlace();
            historian.newItem(tokenForPlace(newPlace), false);
          }
        });

    final HandlerRegistration historyReg =
        historian.addValueChangeHandler(new ValueChangeHandler<String>() {
          public void onValueChange(ValueChangeEvent<String> event) {
            String token = event.getValue();
            handleHistoryToken(token);
          }
        });

    return new HandlerRegistration() {
      public void removeHandler() {
        MyPlaceHistoryHandler.this.defaultPlace = Place.NOWHERE;
        MyPlaceHistoryHandler.this.placeController = null;
        placeReg.removeHandler();
        historyReg.removeHandler();
      }
    };
  }

  private Map<String, String> getParameters(String queryString) {
	  Map<String, String> parameters = new HashMap<String, String>();
	  String[] keyValues = queryString.split("&");
	  for(String keyValue : keyValues) {
		  int firstEquals = keyValue.indexOf("=");
		  String key = keyValue.substring(0, firstEquals);
		  String value = keyValue.substring(firstEquals + 1, keyValue.length());
		  parameters.put(key, value);
	  }
	  return parameters;
  }
  
  private void handleHistoryToken(String token) {
	  Place newPlace = null;
	  
	  if ("".equals(token)) {
        newPlace = defaultPlace;
      }
	  
	  //Google OAuth landing here //TODO: Use OAuth2 for Web Server Applications https://developers.google.com/accounts/docs/OAuth2WebServer 
	  if(token.startsWith("state=")) {
		  token = URL.decodeQueryString(token);
		  Map<String, String> parameters = getParameters(token);		  
		  String accessToken = parameters.get("access_token");
		  String place = parameters.get("state");
		  String action = parameters.get("action");
		  if(place != null && action != null) {
			  switch(action) {
			  case "login":
				  if(accessToken != null) {
					  Authentication.getInstance().setExternalAccessToken(accessToken);
					  eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.TO_BE_DETERMINED));
				  }
				  break;
				  
			  case "settings_save_oto":
				  newPlace = new SettingsPlace(action, accessToken);
				  break;

			  case "settings_create_oto":
				  newPlace = new SettingsPlace(action, accessToken);
				  break;
			  }
		  }
	  }
	  
    
    if (newPlace == null) {
      newPlace = mapper.getPlace(token);
    }

    if (newPlace == null) {
      //log.warning("Unrecognized history token: " + token);
      newPlace = defaultPlace;
    }

    placeController.goTo(newPlace);
  }

  private String tokenForPlace(Place newPlace) {
    if (defaultPlace.equals(newPlace)) {
      return "";
    }

    String token = mapper.getToken(newPlace);
    if (token != null) {
      return token;
    }

   // log.warning("Place not mapped to a token: " + newPlace);
    return "";
  }
}
