package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.client.event.ETCSiteEvent;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class MatrixGenerationEvent extends GwtEvent<MatrixGenerationEventHandler> implements ETCSiteEvent {

	public static Type<MatrixGenerationEventHandler> TYPE = new Type<MatrixGenerationEventHandler>();
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
		
	public MatrixGenerationEvent() { }
	
	public MatrixGenerationEvent(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
	}
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration() {
		return matrixGenerationConfiguration;
	}

	public void setMatrixGenerationConfiguration(MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
	}

	@Override
	public Type<MatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MatrixGenerationEventHandler handler) {
		handler.onMatrixGeneration(this);
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
		return HistoryState.MATRIX_GENERATION;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}
}
