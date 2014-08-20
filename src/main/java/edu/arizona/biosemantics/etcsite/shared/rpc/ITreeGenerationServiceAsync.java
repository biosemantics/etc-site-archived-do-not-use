package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface ITreeGenerationServiceAsync extends IHasTasksServiceAsync {

	public void getTreeGenerationTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> asyncCallback);

}
