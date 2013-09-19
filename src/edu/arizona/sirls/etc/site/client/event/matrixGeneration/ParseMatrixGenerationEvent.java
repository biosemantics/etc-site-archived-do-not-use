package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

public class ParseMatrixGenerationEvent extends GwtEvent<ParseMatrixGenerationEventHandler> {

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

}
