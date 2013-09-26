package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class ParseMatrixGenerationEvent extends GwtEvent<ParseMatrixGenerationEventHandler> implements ETCSiteEvent {

	public static Type<ParseMatrixGenerationEventHandler> TYPE = 
			new Type<ParseMatrixGenerationEventHandler>();
	
	@Override
	public Type<ParseMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ParseMatrixGenerationEventHandler handler) {
		handler.onParse(this);
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
		return HistoryState.PARSE_MATRIX_GENERATION;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
