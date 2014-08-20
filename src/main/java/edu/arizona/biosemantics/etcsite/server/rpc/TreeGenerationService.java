package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITreeGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class TreeGenerationService extends RemoteServiceServlet implements ITreeGenerationService {
	
	@Override
	public RPCResult<Task> getLatestResumable(
			AuthenticationToken authenticationToken) {
		return new RPCResult<Task>(true, "", null);
	}

	@Override
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken,
			Task task) {
		return new RPCResult<Void>(true, "", null);
	}

	@Override
	public RPCResult<Task> getTreeGenerationTask(AuthenticationToken authenticationToken, Task task) {
		return new RPCResult<Task>(true, "", null);
	}

}
