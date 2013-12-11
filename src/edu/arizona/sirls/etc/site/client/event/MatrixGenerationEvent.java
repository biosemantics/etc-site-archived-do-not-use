package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class MatrixGenerationEvent extends GwtEvent<MatrixGenerationEventHandler> implements IETCSiteEvent {

	public static Type<MatrixGenerationEventHandler> TYPE = new Type<MatrixGenerationEventHandler>();
	private Task task;
	
	public MatrixGenerationEvent(Task task) {
		this.task = task;
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

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public boolean hasTask() {
		return this.task != null;
	}
	
}
