package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class HelpEvent extends GwtEvent<HelpEventHandler> {

	public static Type<HelpEventHandler> TYPE = new Type<HelpEventHandler>();
	
	@Override
	public Type<HelpEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HelpEventHandler handler) {
		handler.onHelp(this);
	}
	
}
