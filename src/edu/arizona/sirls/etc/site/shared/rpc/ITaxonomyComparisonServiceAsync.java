package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public interface ITaxonomyComparisonServiceAsync {

	public void getTaxonomyComparisonTask(AuthenticationToken authenticationToken, Task task, AsyncCallback<RPCResult<Task>> asyncCallback);

}
