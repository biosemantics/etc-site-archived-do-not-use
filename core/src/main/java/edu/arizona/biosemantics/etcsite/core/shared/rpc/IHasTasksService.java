package edu.arizona.biosemantics.etcsite.core.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;

public interface IHasTasksService {

	public Task getLatestResumable(AuthenticationToken authenticationToken);	
	
	public List<Task> getResumables(AuthenticationToken authenticationToken);

	public void cancel(AuthenticationToken authenticationToken, Task task) throws Exception;
}
