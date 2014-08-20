package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

@RemoteServiceRelativePath("treeGeneration")
public interface ITreeGenerationService extends RemoteService, IHasTasksService {
	
	public RPCResult<Task> getTreeGenerationTask(AuthenticationToken authenticationToken, Task task);

}
