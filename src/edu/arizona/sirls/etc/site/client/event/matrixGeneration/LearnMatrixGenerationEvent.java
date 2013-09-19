package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

public class LearnMatrixGenerationEvent extends GwtEvent<LearnMatrixGenerationEventHandler> {

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

}
