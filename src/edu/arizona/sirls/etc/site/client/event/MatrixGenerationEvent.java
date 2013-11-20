package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.AbstractTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.SemanticMarkupTaskRun;

public class MatrixGenerationEvent extends GwtEvent<MatrixGenerationEventHandler> implements IETCSiteEvent {

	public static Type<MatrixGenerationEventHandler> TYPE = new Type<MatrixGenerationEventHandler>();
	private MatrixGenerationTaskRun taskRun;
	
	public MatrixGenerationEvent(MatrixGenerationTaskRun taskRun) {
		this.taskRun = taskRun;
	}

	public MatrixGenerationEvent() {}

	@Override
	public Type<MatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MatrixGenerationEventHandler handler) {
		handler.onMatrixGeneration(this);
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

	public MatrixGenerationTaskRun getTaskRun() {
		return taskRun;
	}

	public void setTaskRun(MatrixGenerationTaskRun taskRun) {
		this.taskRun = taskRun;
	}
	
}
