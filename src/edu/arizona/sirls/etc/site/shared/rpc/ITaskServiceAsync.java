package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

public interface ITaskServiceAsync {

	public void getAllTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getCreatedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
	public void getSharedTasks(AuthenticationToken authenticationToken, AsyncCallback<List<Task>> callback);
	
}
