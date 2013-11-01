package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public RPCResult<MatrixGenerationTaskRun> start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName);
	
	public RPCResult<List<PreprocessedDescription>> preprocess(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public RPCResult<LearnInvocation> learn(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);

	public RPCResult<MatrixGenerationTaskRun> review(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public RPCResult<ParseInvocation> parse(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public RPCResult<Boolean> output(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);

	public RPCResult<MatrixGenerationTaskRun> goToTaskStage(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, TaskStageEnum taskStage);

	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String target);
	
	public RPCResult<Boolean> setDescription(AuthenticationToken authenticationToken, String target, String description);
	
	public RPCResult<MatrixGenerationTaskRun> getLatestResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<MatrixGenerationTaskRun> getMatrixGenerationTaskRun(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);

	
	
	/*public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob); */
}
