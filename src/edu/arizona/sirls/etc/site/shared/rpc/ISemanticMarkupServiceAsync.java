package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.PreprocessedDescription;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.TaskStageEnum;

public interface ISemanticMarkupServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String filePath, String glossaryName, AsyncCallback<RPCResult<SemanticMarkupTaskRun>> callback);
	
	public void preprocess(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, AsyncCallback<RPCResult<List<PreprocessedDescription>>> callback);

	public void learn(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, AsyncCallback<RPCResult<LearnInvocation>> callback);
	
	public void review(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, AsyncCallback<RPCResult<SemanticMarkupTaskRun>> callback);

	public void parse(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, AsyncCallback<RPCResult<ParseInvocation>> callback);
	
	public void output(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, AsyncCallback<RPCResult<Void>> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, SemanticMarkupTaskRun semanticMarkupTask, TaskStageEnum taskStage, 
			AsyncCallback<RPCResult<SemanticMarkupTaskRun>> callback);
	
	public void getDescription(AuthenticationToken authenticationToken, String filePath, AsyncCallback<RPCResult<String>> callback);
	
	public void setDescription(AuthenticationToken authenticationToken, String filePath, String description, AsyncCallback<RPCResult<Void>> callback);

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<SemanticMarkupTaskRun>> asyncCallback);

	public void getSemanticMarkupTaskRun(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<SemanticMarkupTaskRun>> callback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

}
