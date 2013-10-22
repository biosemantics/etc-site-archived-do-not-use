package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class HomeEvent extends GwtEvent<HomeEventHandler> implements IETCSiteEvent {

	public static Type<HomeEventHandler> TYPE = new Type<HomeEventHandler>();
	
	@Override
	public Type<HomeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HomeEventHandler handler) {
		handler.onHome(this);
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.HOME;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
	
}
