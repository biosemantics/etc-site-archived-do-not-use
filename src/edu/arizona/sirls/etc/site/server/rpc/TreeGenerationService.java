package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.TreeGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.VisualizationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TreeGenerationConfiguration;

public class TreeGenerationService extends RemoteServiceServlet implements ITreeGenerationService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public RPCResult<TreeGenerationTaskRun> getTreeGenerationTask(AuthenticationToken authenticationToken, Task task) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<TreeGenerationTaskRun>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<TreeGenerationTaskRun>(false, "Authentication failed");
		
		return null;
	}

}
