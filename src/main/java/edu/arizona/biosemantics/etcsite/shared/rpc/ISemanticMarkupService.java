package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.ParseInvocation;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;

@RemoteServiceRelativePath("semanticMarkup")
public interface ISemanticMarkupService extends RemoteService {

	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName);
	
	public RPCResult<List<PreprocessedDescription>> preprocess(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<LearnInvocation> learn(AuthenticationToken authenticationToken, Task semanticMarkupTask);

	public RPCResult<Task> review(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<ParseInvocation> parse(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<Task> output(AuthenticationToken authenticationToken, Task semanticMarkupTask);

	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task semanticMarkupTask, TaskStageEnum taskStage);

	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Void> setDescription(AuthenticationToken authenticationToken, String filePath, String description);
	
	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);

}
