package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;

public class MatrixGenerationEvent extends GwtEvent<MatrixGenerationEventHandler> implements IETCSiteEvent, ITaskEvent<MatrixGenerationTaskRun> {

	public static Type<MatrixGenerationEventHandler> TYPE = new Type<MatrixGenerationEventHandler>();
	private MatrixGenerationTaskRun taskConfiguration;
		
	public MatrixGenerationEvent() { }
	
	public MatrixGenerationEvent(MatrixGenerationTaskRun taskConfiguration) {
		this.taskConfiguration = taskConfiguration;
	}
	
	@Override
	public MatrixGenerationTaskRun getTaskConfiguration() {
		return taskConfiguration;
	}

	@Override
	public void setTaskConfiguration(MatrixGenerationTaskRun task) {
		this.taskConfiguration = task;
	}

	@Override
	public Type<MatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MatrixGenerationEventHandler handler) {
		handler.onMatrixGeneration(this);
	}

	@Override
	public boolean hasTaskConfiguration() {
		return this.taskConfiguration != null;
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
