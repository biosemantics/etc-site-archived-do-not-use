package edu.arizona.biosemantics.etcsite.shared.rpc.ontologize;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;

public interface IOntologizeServiceAsync extends IHasTasksServiceAsync {

	void build(AuthenticationToken token, Task task, AsyncCallback<Collection> callback);

	void downloadOntologize(AuthenticationToken token, Task task, AsyncCallback<String> callback);
	
	void startWithOntologyCreation(AuthenticationToken token, String taskName, String input,
			String taxonGroup, AsyncCallback<Task> callback);

	void isValidInput(AuthenticationToken token, String inputFolderPath,
			AsyncCallback<Boolean> callback);

	void goToTaskStage(AuthenticationToken token, Task task, TaskStageEnum taskStageEnum, 
			AsyncCallback<Task> callback);

	void isValidOntology(AuthenticationToken authenticationToken, String ontologyPath, 
			AsyncCallback<Boolean> callback);

	void output(AuthenticationToken token, Task task,
			AsyncCallback<Task> callback);

	void addInput(AuthenticationToken token, Task task, String inputFile, AsyncCallback<Collection> callback);
}
