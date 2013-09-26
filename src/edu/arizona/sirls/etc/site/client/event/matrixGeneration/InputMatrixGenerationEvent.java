package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;

public class InputMatrixGenerationEvent extends GwtEvent<InputMatrixGenerationEventHandler> implements ETCSiteEvent {

	public static Type<InputMatrixGenerationEventHandler> TYPE = 
			new Type<InputMatrixGenerationEventHandler>();
	
	@Override
	public Type<InputMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(InputMatrixGenerationEventHandler handler) {
		handler.onInput(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.INPUT_MATRIX_GENERATION;
	}
	
	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
