package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IPipelineService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IVisualizationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

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
