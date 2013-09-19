package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

public class OutputMatrixGenerationEvent extends GwtEvent<OutputMatrixGenerationEventHandler> {

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

}
