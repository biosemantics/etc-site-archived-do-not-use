package edu.arizona.biosemantics.etcsite.shared.rpc;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;

public interface IHasTasksService {

	public Task getLatestResumable(AuthenticationToken authenticationToken);	

	public void cancel(AuthenticationToken authenticationToken, Task task) throws Exception;
}
