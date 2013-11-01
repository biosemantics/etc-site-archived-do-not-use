package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface ITreeGenerationServiceAsync {

	public void getTreeGenerationTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<TreeGenerationTaskRun>> asyncCallback);

}
