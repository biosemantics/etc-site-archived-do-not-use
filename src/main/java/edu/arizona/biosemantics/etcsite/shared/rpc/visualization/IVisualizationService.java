package edu.arizona.biosemantics.etcsite.shared.rpc.visualization;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("visualization")
public interface IVisualizationService extends RemoteService, IHasTasksService {

	public Task getVisualizationTask(AuthenticationToken authenticationToken, Task task);

}
