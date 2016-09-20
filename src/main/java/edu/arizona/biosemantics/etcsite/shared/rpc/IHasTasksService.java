package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;

public interface IHasTasksService {

	public Task getLatestResumable(AuthenticationToken authenticationToken);	
	
	public List<Task> getResumables(AuthenticationToken authenticationToken);

	public void cancel(AuthenticationToken authenticationToken, Task task) throws Exception;
}
