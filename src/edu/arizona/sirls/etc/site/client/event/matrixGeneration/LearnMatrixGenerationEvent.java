package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class LearnMatrixGenerationEvent extends GwtEvent<LearnMatrixGenerationEventHandler> implements ETCSiteEvent {

	public static Type<LearnMatrixGenerationEventHandler> TYPE = 
			new Type<LearnMatrixGenerationEventHandler>();

	@Override
	public Type<LearnMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LearnMatrixGenerationEventHandler handler) {
		handler.onLearn(this);
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
		return HistoryState.LEARN_MATRIX_GENERATION;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
