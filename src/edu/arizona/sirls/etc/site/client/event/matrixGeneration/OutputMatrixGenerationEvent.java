package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class OutputMatrixGenerationEvent extends GwtEvent<OutputMatrixGenerationEventHandler> implements ETCSiteEvent {

	public static Type<OutputMatrixGenerationEventHandler> TYPE = 
			new Type<OutputMatrixGenerationEventHandler>();
	
	@Override
	public Type<OutputMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OutputMatrixGenerationEventHandler handler) {
		handler.onOutput(this);
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
		return HistoryState.OUTPUT_MATRIX_GENERATION;
	}
	
	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}

}
