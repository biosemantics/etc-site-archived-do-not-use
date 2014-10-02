package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.ParseInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;

@RemoteServiceRelativePath("semanticMarkup")
public interface ISemanticMarkupService extends RemoteService, IHasTasksService {

	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName);
	
	public RPCResult<Boolean> isValidInput(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<List<PreprocessedDescription>> preprocess(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<LearnInvocation> learn(AuthenticationToken authenticationToken, Task semanticMarkupTask);

	public RPCResult<Task> review(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<ParseInvocation> parse(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public RPCResult<Task> output(AuthenticationToken authenticationToken, Task semanticMarkupTask);

	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task semanticMarkupTask, TaskStageEnum taskStage);

	public RPCResult<String> getDescription(AuthenticationToken authenticationToken, String filePath);
	
	public RPCResult<Void> setDescription(AuthenticationToken authenticationToken, String filePath, String description);
	
	public RPCResult<String> saveOto(AuthenticationToken authenticationToken, Task task);
	
	//public RPCResult<Void> prepareOptionalOtoLiteSteps(AuthenticationToken authenticationToken, Task task);

}
