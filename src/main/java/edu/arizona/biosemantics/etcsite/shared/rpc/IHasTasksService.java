package edu.arizona.biosemantics.etcsite.shared.rpc;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface IHasTasksService {

	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken);	

	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);
}
