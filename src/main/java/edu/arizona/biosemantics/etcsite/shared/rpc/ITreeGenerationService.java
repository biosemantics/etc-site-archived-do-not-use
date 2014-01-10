package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

@RemoteServiceRelativePath("treeGeneration")
public interface ITreeGenerationService extends RemoteService  {
	
	public RPCResult<Task> getTreeGenerationTask(AuthenticationToken authenticationToken, Task task);

}
