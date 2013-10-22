package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class PipelineEvent extends GwtEvent<PipelineEventHandler> implements IETCSiteEvent {
	
	public static Type<PipelineEventHandler> TYPE = new Type<PipelineEventHandler>();
	
	
	@Override
	public GwtEvent.Type<PipelineEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PipelineEventHandler handler) {
		handler.onPipeline(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.PIPELINE;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
	

}
