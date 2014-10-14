package edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface ITreeGenerationServiceAsync extends IHasTasksServiceAsync {

	public void getTreeGenerationTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Task> callback);

}
