package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

@RemoteServiceRelativePath("visualization")
public interface IVisualizationService extends RemoteService, IHasTasksService {

	public RPCResult<Task> getVisualizationTask(AuthenticationToken authenticationToken, Task task);

}
