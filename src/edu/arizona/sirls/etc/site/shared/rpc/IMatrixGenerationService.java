package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.PreprocessedDescription;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String input);
	
	public RPCResult<Task> process(AuthenticationToken authenticationToken, Task task);
	
	public RPCResult<Void> output(AuthenticationToken authenticationToken, Task task);

	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken);
	
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task);

}
