package edu.arizona.sirls.etc.site.client.event.matrixGeneration;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class MatrixGenerationEvent extends GwtEvent<MatrixGenerationEventHandler> {

	public static Type<MatrixGenerationEventHandler> TYPE = new Type<MatrixGenerationEventHandler>();
	private Task task;
	
	public MatrixGenerationEvent(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public Type<MatrixGenerationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MatrixGenerationEventHandler handler) {
		handler.onMatrixGeneration(this);
	}

}
