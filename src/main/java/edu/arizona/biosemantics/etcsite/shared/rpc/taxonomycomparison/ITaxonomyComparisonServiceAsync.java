package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;

public interface ITaxonomyComparisonServiceAsync extends IHasTasksServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String input, AsyncCallback<Task> callback);

	public void isValidInput(AuthenticationToken token, String inputFile,
			AsyncCallback<Boolean> callback);
	
	public void runMirGeneration(AuthenticationToken token, Task task,
			Model model, AsyncCallback<Task> asyncCallback);

	public void getInputVisualization(AuthenticationToken token, Task task,
			Model model, AsyncCallback<String> callback);

	public void getMirGenerationResult(AuthenticationToken token, Task task, AsyncCallback<RunOutput> callback);

	public void goToTaskStage(AuthenticationToken token, Task task,
			TaskStageEnum taskStageEnum, AsyncCallback<Task> callback);
	

	public void getModel(AuthenticationToken token, Task task, AsyncCallback<Model> callback);

	public void saveModel(AuthenticationToken token, Task task, Model model, AsyncCallback<Void> callback);

	public void exportArticulations(AuthenticationToken token, Task task, Model model, AsyncCallback<String> callback);
	
	public void getTaxonomies(AuthenticationToken token, FolderTreeItem folder, AsyncCallback<List<String>> callback);
}
