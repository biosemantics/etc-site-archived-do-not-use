package edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public interface IMatrixGenerationServiceAsync extends IHasTasksServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String filePath, AsyncCallback<RPCResult<Task>> callback);
	
	public void process(AuthenticationToken authenticationToken,  Task task, AsyncCallback<RPCResult<Task>> callback);
	
	public void review(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Matrix>> callback);
	
	public void completeReview(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> callback);
	
	public void output(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> callback);
	
	public void save(AuthenticationToken authenticationToken, Matrix matrix, Task task, AsyncCallback<RPCResult<Void>> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum review, AsyncCallback<RPCResult<Task>> callback);

	public void isValidInput(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<Boolean>> callback);

}
