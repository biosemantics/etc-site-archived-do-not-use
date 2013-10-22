package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonService;
import edu.arizona.sirls.etc.site.shared.rpc.TaxonomyComparisonTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonService extends RemoteServiceServlet implements ITaxonomyComparisonService {

	@Override
	public TaxonomyComparisonTaskRun getTaxonomyComparisonTask(AuthenticationToken authenticationToken, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

}
