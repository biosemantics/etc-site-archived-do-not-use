package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface IHasTasksServiceAsync {

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Task>> asyncCallback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);
	
}
