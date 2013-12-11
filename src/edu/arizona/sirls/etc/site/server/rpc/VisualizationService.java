package edu.arizona.sirls.etc.site.server.rpc;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class VisualizationService extends RemoteServiceServlet implements IVisualizationService {
	
	@Override
	public RPCResult<Task> getVisualizationTask(AuthenticationToken authenticationToken, Task task) {		
		return null;
	}

}
