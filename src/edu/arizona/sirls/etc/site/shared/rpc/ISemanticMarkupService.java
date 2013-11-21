package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.TaskStageEnum;

@RemoteServiceRelativePath("semanticMarkup")
public interface ISemanticMarkupService extends RemoteService {

	public RPCResult<SemanticMarkupTaskRun> start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName);
	
	public RPCResult<List<PreprocessedDescription>> preprocess(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask);
	
	public RPCResult<LearnInvocation> learn(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask);

	public RPCResult<SemanticMarkupTaskRun> review(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask);
	
	public RPCResult<ParseInvocation> parse(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask);
	
	public RPCResult<Void> output(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask);

	public RPCResult<SemanticMarkupTaskRun> goToTaskStage(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, TaskStageEnum taskStage);

	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Void> setDescription(AuthenticationToken authenticationToken, String filePath, String description);
	
	public RPCResult<SemanticMarkupTaskRun> getLatestResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<SemanticMarkupTaskRun> getSemanticMarkupTaskRun(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);

}
