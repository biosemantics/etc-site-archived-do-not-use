package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITreeGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class TreeGenerationService extends RemoteServiceServlet implements ITreeGenerationService {
	
	@Override
	public RPCResult<Task> getTreeGenerationTask(AuthenticationToken authenticationToken, Task task) {
		
		return null;
	}

}
