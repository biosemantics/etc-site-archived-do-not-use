package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String input);
	
	public RPCResult<Task> process(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Matrix> review(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Task> completeReview(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Task> output(AuthenticationToken authenticationToken, Task task);

	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);

	public RPCResult<Void> save(AuthenticationToken authenticationToken, Matrix matrix, Task task);
	
	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum review);

}
