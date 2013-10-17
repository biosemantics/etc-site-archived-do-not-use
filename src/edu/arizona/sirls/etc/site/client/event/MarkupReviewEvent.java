package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class MarkupReviewEvent extends GwtEvent<MarkupReviewEventHandler> implements ETCSiteEvent {

	public static Type<MarkupReviewEventHandler> TYPE = new Type<MarkupReviewEventHandler>();
	
	@Override
	public Type<MarkupReviewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MarkupReviewEventHandler handler) {
		handler.onMarkupReview(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.MARKUP_REVIEW;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}

}
