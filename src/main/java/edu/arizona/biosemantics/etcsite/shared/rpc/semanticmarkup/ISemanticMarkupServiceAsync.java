package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface ISemanticMarkupServiceAsync extends IHasTasksServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String filePath, String taxonGroup, boolean emptyGlossary, AsyncCallback<Task> callback);
	
	public void checkValidInput(AuthenticationToken authenticationToken, String filePath, AsyncCallback<String> callback);
	
	public void preprocess(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<List<PreprocessedDescription>> callback);

	public void learn(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<LearnInvocation> callback);
	
	public void review(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<Task> callback);

	public void parse(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<Void> callback);
	
	public void output(AuthenticationToken authenticationToken, Task semanticMarkupTask, AsyncCallback<Task> callback);

	public void goToTaskStage(AuthenticationToken authenticationToken, Task semanticMarkupTask, TaskStageEnum taskStage, 
			AsyncCallback<Task> callback);
	
	public void getDescriptions(AuthenticationToken authenticationToken, String filePath, AsyncCallback<List<Description>> callback);
	
	public void getDescription(AuthenticationToken token, String filePath, int descriptionNumber, AsyncCallback<Description> asyncCallback);
	
	public void setDescription(AuthenticationToken authenticationToken, String filePath, int descriptionNumber, String description, AsyncCallback<Void> callback);

	public void saveOto(AuthenticationToken authenticationToken, Task task, AsyncCallback<String> callback);

	public void renameTerm(AuthenticationToken token, Task task, String term, String newName, AsyncCallback<Void> callback);

	public void importOto(Task task, String termCategorization, String synonymy, AsyncCallback<Void> callback);
	
	public void sendToOto(AuthenticationToken token, Task task,	AsyncCallback<Void> callback);

	public void isLargeInput(AuthenticationToken token, String inputFile, AsyncCallback<Boolean> asyncCallback);
	
}
