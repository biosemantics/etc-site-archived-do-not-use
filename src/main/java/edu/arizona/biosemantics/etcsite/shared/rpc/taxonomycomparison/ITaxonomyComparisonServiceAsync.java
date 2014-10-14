package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface ITaxonomyComparisonServiceAsync extends IHasTasksServiceAsync {

	public void getTaxonomyComparisonTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<Task> callback);

}
