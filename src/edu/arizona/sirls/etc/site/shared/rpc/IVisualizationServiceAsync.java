package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;

public interface IVisualizationServiceAsync {

	public void getVisualizationTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<VisualizationTaskRun>> asyncCallback);

}
