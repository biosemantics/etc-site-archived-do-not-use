package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SettingsEvent extends GwtEvent<SettingsEventHandler> {
	
	public static Type<SettingsEventHandler> TYPE = new Type<SettingsEventHandler>();
	
	@Override
	public Type<SettingsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SettingsEventHandler handler) {
		handler.onSettings(this);
	}
	
}
