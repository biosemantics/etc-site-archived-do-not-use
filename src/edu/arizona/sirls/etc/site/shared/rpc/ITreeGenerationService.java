package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;

@RemoteServiceRelativePath("treeGeneration")
public interface ITreeGenerationService extends RemoteService  {
	
	public TreeGenerationTaskRun getTreeGenerationTask(AuthenticationToken authenticationToken, Task task);

}
