package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IVisualizationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class VisualizationService extends RemoteServiceServlet implements IVisualizationService {
	
	@Override
	public RPCResult<Task> getVisualizationTask(AuthenticationToken authenticationToken, Task task) {		
		return null;
	}

}
