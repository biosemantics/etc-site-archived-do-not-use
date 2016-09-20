package edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.treegeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public interface ITreeGenerationServiceAsync extends IHasTasksServiceAsync {

	public void isValidInput(AuthenticationToken token, String inputFile,
			AsyncCallback<Boolean> asyncCallback);

	public void start(AuthenticationToken token, String taskName,
			String inputFile, AsyncCallback<Task> asyncCallback);
	
	public void view(AuthenticationToken authenticationToken, Task task, AsyncCallback<TaxonMatrix> callback);

	public void goToTaskStage(AuthenticationToken token, Task task,
			TaskStageEnum view, AsyncCallback<Task> asyncCallback);

}
