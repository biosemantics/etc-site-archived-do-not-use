package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface IVisualizationServiceAsync extends IHasTasksServiceAsync {

	public void getVisualizationTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> asyncCallback);

}
