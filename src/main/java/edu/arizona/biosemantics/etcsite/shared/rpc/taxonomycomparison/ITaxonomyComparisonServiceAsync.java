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
	
	public void process(AuthenticationToken token, Task task,
			AsyncCallback<Task> asyncCallback);
	
	public void getInput(AuthenticationToken token, Task task, AsyncCallback<Model> callback);
}
