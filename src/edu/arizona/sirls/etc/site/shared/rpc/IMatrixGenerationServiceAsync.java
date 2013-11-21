package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface IMatrixGenerationServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, String filePath, AsyncCallback<RPCResult<Task>> callback);
	
	public void process(AuthenticationToken authenticationToken,  Task task, AsyncCallback<RPCResult<Task>> callback);
	
	public void output(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<RPCResult<Task>> asyncCallback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Void>> callback);

}
