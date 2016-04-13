package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IHasTasksServiceAsync {

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<Task> callback);
	
	public void getResumables(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);
	
}
