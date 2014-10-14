package edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("treeGeneration")
public interface ITreeGenerationService extends RemoteService, IHasTasksService {
	
	public Task getTreeGenerationTask(AuthenticationToken authenticationToken, Task task);

}
