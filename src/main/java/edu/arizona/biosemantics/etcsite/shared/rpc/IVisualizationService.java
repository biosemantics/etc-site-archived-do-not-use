package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

@RemoteServiceRelativePath("visualization")
public interface IVisualizationService extends RemoteService {

	public RPCResult<Task> getVisualizationTask(AuthenticationToken authenticationToken, Task task);

}
