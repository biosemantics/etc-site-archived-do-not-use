package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("taxonomyComparison")
public interface ITaxonomyComparisonService extends RemoteService, IHasTasksService {

	public Task start(AuthenticationToken authenticationToken, String taskName, String input)
			throws TaxonomyComparisonException;
	
	public boolean isValidInput(AuthenticationToken token, String inputFile);
	
	public Task process(AuthenticationToken token, Task task);
	
	

}
