package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

@RemoteServiceRelativePath("taxonomyComparison")
public interface ITaxonomyComparisonService extends RemoteService, IHasTasksService {
	
	

}
