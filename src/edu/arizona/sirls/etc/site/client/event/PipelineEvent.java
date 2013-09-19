package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PipelineEvent extends GwtEvent<PipelineEventHandler> {
	
	public static Type<PipelineEventHandler> TYPE = new Type<PipelineEventHandler>();
	
	
	@Override
	public GwtEvent.Type<PipelineEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PipelineEventHandler handler) {
		handler.onPipeline(this);
	}

}
