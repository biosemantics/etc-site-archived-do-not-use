package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;

public interface ITaxonomyComparisonServiceAsync extends IHasTasksServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String input, AsyncCallback<Task> callback);

	public void isValidInput(AuthenticationToken token, String inputFile,
			AsyncCallback<Boolean> asyncCallback);
	
	public void getInput(AuthenticationToken token, Task task, AsyncCallback<Model> callback);

	public void runMirGeneration(AuthenticationToken token, Task task,
			Model model, AsyncCallback<Task> asyncCallback);

	public void getInputVisualization(AuthenticationToken token, Task task,
			Model model, AsyncCallback<String> asyncCallback);

	public void getMirGenerationResult(AuthenticationToken token, Task task, AsyncCallback<MIRGenerationResult> asyncCallback);
}
