package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class LogoutEvent extends GwtEvent<LogoutEventHandler> implements ETCSiteEvent {

	public static Type<LogoutEventHandler> TYPE = new Type<LogoutEventHandler>();
	
	@Override
	public Type<LogoutEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LogoutEventHandler handler) {
		handler.onLogout(this);
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}

	@Override
	public HistoryState getHistoryState() {
		return null;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
	
}
