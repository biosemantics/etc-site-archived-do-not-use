package edu.arizona.biosemantics.etcsite.shared.rpc.visualization;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IVisualizationServiceAsync extends IHasTasksServiceAsync {

	public void getVisualizationTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Task> callback);

}
