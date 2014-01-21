package edu.arizona.biosemantics.otolite.client.event.to_ontologies;

import com.google.gwt.event.shared.GwtEvent;

public class ClearSelectionEvent extends GwtEvent<ClearSelectionEventHandler> {

	public static Type<ClearSelectionEventHandler> TYPE = new Type<ClearSelectionEventHandler>();

	@Override
	public Type<ClearSelectionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ClearSelectionEventHandler handler) {
		handler.onClick(this);
	}

}
