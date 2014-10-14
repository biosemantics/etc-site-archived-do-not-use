package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;

public class TaxonomyComparisonService extends RemoteServiceServlet implements ITaxonomyComparisonService {
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Task getLatestResumable(
			AuthenticationToken authenticationToken) {
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken,	Task task) {
	}

	@Override
	public Task getTaxonomyComparisonTask(
			AuthenticationToken authenticationToken, Task task) {
		return null;
	}

}
