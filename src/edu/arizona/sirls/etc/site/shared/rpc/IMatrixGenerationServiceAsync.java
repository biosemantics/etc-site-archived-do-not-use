package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public interface IMatrixGenerationServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName, AsyncCallback<RPCResult<MatrixGenerationTaskRun>> callback);
	
	public void preprocess(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, AsyncCallback<RPCResult<List<PreprocessedDescription>>> callback);

	public void learn(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, AsyncCallback<RPCResult<LearnInvocation>> callback);
	
	public void review(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, AsyncCallback<RPCResult<MatrixGenerationTaskRun>> callback);

	public void parse(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, AsyncCallback<RPCResult<ParseInvocation>> callback);
	
	public void output(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, AsyncCallback<RPCResult<Void>> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, TaskStageEnum taskStage, 
			AsyncCallback<RPCResult<MatrixGenerationTaskRun>> callback);
	
	public void getDescription(AuthenticationToken authenticationToken, String target, AsyncCallback<RPCResult<String>> callback);
	
	public void setDescription(AuthenticationToken authenticationToken, String target, String description, AsyncCallback<RPCResult<Void>> callback);

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<MatrixGenerationTaskRun>> asyncCallback);

	public void getMatrixGenerationTaskRun(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<MatrixGenerationTaskRun>> callback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

	
	
	/*public void getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob, AsyncCallback<MatrixGenerationJobStatus> callback);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob, AsyncCallback<Void> callback);
	*/
}
