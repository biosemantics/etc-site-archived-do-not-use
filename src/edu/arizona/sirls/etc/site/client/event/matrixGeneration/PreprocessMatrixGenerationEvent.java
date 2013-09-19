package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

public class PreprocessMatrixGenerationEvent extends GwtEvent<PreprocessMatrixGenerationEventHandler> {

	public static Type<PreprocessMatrixGenerationEventHandler> TYPE = 
			new Type<PreprocessMatrixGenerationEventHandler>();
	
	@Override
	public Type<PreprocessMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PreprocessMatrixGenerationEventHandler handler) {
		handler.onPreprocess(this);
	}

}
