package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class VisualizationEvent extends GwtEvent<VisualizationEventHandler> {

	public static Type<VisualizationEventHandler> TYPE = new Type<VisualizationEventHandler>();
	
	@Override
	public Type<VisualizationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(VisualizationEventHandler handler) {
		handler.onVisualization(this);
	}
}
