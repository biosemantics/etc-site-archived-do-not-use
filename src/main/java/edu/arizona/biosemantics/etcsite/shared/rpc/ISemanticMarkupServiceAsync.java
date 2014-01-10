package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.ParseInvocation;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;

public interface ISemanticMarkupServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName, AsyncCallback<RPCResult<Task>> callback);
	
	public void preprocess(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<RPCResult<List<PreprocessedDescription>>> callback);

	public void learn(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<RPCResult<LearnInvocation>> callback);
	
	public void review(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<RPCResult<Task>> callback);

	public void parse(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<RPCResult<ParseInvocation>> callback);
	
	public void output(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<RPCResult<Task>> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, Task semanticMarkupTask, TaskStageEnum taskStage, 
			AsyncCallback<RPCResult<Task>> callback);
	
	public void getDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<String>> callback);
	
	public void setDescription(AuthenticationToken authenticationToken, String filePath, String description, AsyncCallback<RPCResult<Void>> callback);

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Task>> asyncCallback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

}
