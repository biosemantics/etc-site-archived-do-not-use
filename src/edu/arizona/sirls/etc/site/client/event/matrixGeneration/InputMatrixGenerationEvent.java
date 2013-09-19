package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

public class InputMatrixGenerationEvent extends GwtEvent<InputMatrixGenerationEventHandler> {

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
}
