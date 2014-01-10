package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum;

public interface IMatrixGenerationServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String filePath, AsyncCallback<RPCResult<Task>> callback);
	
	public void process(AuthenticationToken authenticationToken,  Task task, AsyncCallback<RPCResult<Task>> callback);
	
	public void review(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Matrix>> callback);
	
	public void completeReview(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> callback);
	
	public void output(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> callback);

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Task>> asyncCallback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

	public void save(AuthenticationToken authenticationToken, Matrix matrix, Task task, AsyncCallback<RPCResult<Void>> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum review, AsyncCallback<RPCResult<Task>> callback);


}
