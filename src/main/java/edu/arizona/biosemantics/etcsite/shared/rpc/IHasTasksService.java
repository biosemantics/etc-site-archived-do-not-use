package edu.arizona.biosemantics.etcsite.shared.rpc;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface IHasTasksService {

	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken);	

	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);
}
