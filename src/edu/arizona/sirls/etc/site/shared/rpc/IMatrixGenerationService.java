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

	public MatrixGenerationTaskRun start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName);
	
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);

	public MatrixGenerationTaskRun review(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public ParseInvocation parse(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);
	
	public boolean output(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask);

	public MatrixGenerationTaskRun goToTaskStage(AuthenticationToken authenticationToken, MatrixGenerationTaskRun matrixGenerationTask, TaskStageEnum taskStage);

	public String getDescription(AuthenticationToken authenticationToken, String target);
	
	public boolean setDescription(AuthenticationToken authenticationToken, String target, String description);
	
	public MatrixGenerationTaskRun getLatestResumable(AuthenticationToken authenticationToken);
	
	public MatrixGenerationTaskRun getMatrixGenerationTaskRun(AuthenticationToken authenticationToken, Task task);
	
	public void cancel(AuthenticationToken authenticationToken, Task task);

	
	
	/*public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob); */
}
