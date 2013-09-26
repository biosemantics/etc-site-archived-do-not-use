package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class SettingsEvent extends GwtEvent<SettingsEventHandler> implements ETCSiteEvent {
	
	public static Type<SettingsEventHandler> TYPE = new Type<SettingsEventHandler>();
	
	@Override
	public Type<SettingsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SettingsEventHandler handler) {
		handler.onSettings(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.SETTINGS;
	}
	
	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
