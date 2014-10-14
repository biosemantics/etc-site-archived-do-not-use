package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("semanticMarkup")
public interface ISemanticMarkupService extends RemoteService, IHasTasksService {

	public Task start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName) throws SemanticMarkupException;
	
	public boolean isValidInput(AuthenticationToken authenticationToken, String filePath) throws SemanticMarkupException;
	
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, Task semanticMarkupTask);
	
	public LearnInvocation learn(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;

	public Task review(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;
	
	public void parse(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;
	
	public Task output(AuthenticationToken authenticationToken, Task semanticMarkupTask) throws SemanticMarkupException;

	public Task goToTaskStage(AuthenticationToken authenticationToken, Task semanticMarkupTask, TaskStageEnum taskStage);

	public String getDescription(AuthenticationToken authenticationToken, String filePath);
	
	public void setDescription(AuthenticationToken authenticationToken, String filePath, String description) throws SemanticMarkupException;
	
	public String saveOto(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException;
	
	//public RPCResult<Void> prepareOptionalOtoLiteSteps(AuthenticationToken authenticationToken, Task task);

}
