package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class HelpEvent extends GwtEvent<HelpEventHandler> implements IETCSiteEvent {

	public static Type<HelpEventHandler> TYPE = new Type<HelpEventHandler>();
	
	@Override
	public Type<HelpEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HelpEventHandler handler) {
		handler.onHelp(this);
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.HELP;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
	
}
