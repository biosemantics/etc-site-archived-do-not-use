package edu.arizona.biosemantics.etcsite.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class TaxonomyComparisonService extends RemoteServiceServlet implements ITaxonomyComparisonService {
	
	@Override
	public RPCResult<Task> getTaxonomyComparisonTask(AuthenticationToken authenticationToken, Task task) {
		
		return null;
	}

}
