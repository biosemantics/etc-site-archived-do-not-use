package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IPipelineService;

public class PipelineService extends RemoteServiceServlet implements IPipelineService {
	
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


}
