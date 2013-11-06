package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonService;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.TaxonomyComparisonTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.VisualizationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonService extends RemoteServiceServlet implements ITaxonomyComparisonService {

	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public RPCResult<TaxonomyComparisonTaskRun> getTaxonomyComparisonTask(AuthenticationToken authenticationToken, Task task) {
		RPCResult<AuthenticationResult> authResult = authenticationService.isValidSession(authenticationToken);
		if(!authResult.isSucceeded()) 
			return new RPCResult<TaxonomyComparisonTaskRun>(false, authResult.getMessage());
		if(!authResult.getData().getResult())
			return new RPCResult<TaxonomyComparisonTaskRun>(false, "Authentication failed");
		
		return null;
	}

}
