package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class PreprocessMatrixGenerationEvent extends GwtEvent<PreprocessMatrixGenerationEventHandler> implements ETCSiteEvent {

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

	public MatrixGenerationConfiguration getMatrixGenerationConfiguration() {
		return matrixGenerationConfiguration;
	}

	public void setMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
	}

	public boolean hasMatrixGenerationConfiguration() {
		return this.matrixGenerationConfiguration != null;
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.PREPROCESS_MATRIX_GENERATION;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
