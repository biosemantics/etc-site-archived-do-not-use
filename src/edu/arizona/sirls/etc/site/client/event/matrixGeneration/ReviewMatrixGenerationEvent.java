package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class ReviewMatrixGenerationEvent extends GwtEvent<ReviewMatrixGenerationEventHandler> implements ETCSiteEvent {

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

	public boolean hasMatrixGenerationConfiguration() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.REVIEW_MATRIX_GENERATION;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
