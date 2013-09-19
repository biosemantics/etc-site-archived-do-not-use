package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

public class ReviewMatrixGenerationEvent extends GwtEvent<ReviewMatrixGenerationEventHandler> {

	public static Type<ReviewMatrixGenerationEventHandler> TYPE = 
			new Type<ReviewMatrixGenerationEventHandler>();
	
	@Override
	public Type<ReviewMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ReviewMatrixGenerationEventHandler handler) {
		handler.onReview(this);
	}

}
