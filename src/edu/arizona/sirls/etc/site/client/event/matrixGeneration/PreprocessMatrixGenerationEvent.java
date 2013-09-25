package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class PreprocessMatrixGenerationEvent extends GwtEvent<PreprocessMatrixGenerationEventHandler> {

	public static Type<PreprocessMatrixGenerationEventHandler> TYPE = 
			new Type<PreprocessMatrixGenerationEventHandler>();
	
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
	
	public PreprocessMatrixGenerationEvent(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
	}

	@Override
	public Type<PreprocessMatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PreprocessMatrixGenerationEventHandler handler) {
		handler.onPreprocess(this);
	}

}
