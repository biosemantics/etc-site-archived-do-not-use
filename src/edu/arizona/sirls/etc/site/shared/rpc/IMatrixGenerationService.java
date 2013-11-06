package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public RPCResult<MatrixGenerationTaskRun> start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName);
	
	public RPCResult<List<PreprocessedDescription>> preprocess(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public RPCResult<LearnInvocation> learn(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);

	public RPCResult<MatrixGenerationTaskRun> review(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public RPCResult<ParseInvocation> parse(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public RPCResult<Void> output(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);

	public RPCResult<MatrixGenerationTaskRun> goToTaskStage(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, TaskStageEnum taskStage);

	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Void> setDescription(AuthenticationToken authenticationToken, String filePath, String description);
	
	public RPCResult<MatrixGenerationTaskRun> getLatestResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<MatrixGenerationTaskRun> getMatrixGenerationTaskRun(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);

}
