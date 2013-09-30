package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;

public interface IMatrixGenerationServiceAsync {

	public void start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName, AsyncCallback<MatrixGenerationConfiguration> callback);
	
	public void preprocess(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<List<PreprocessedDescription>> callback);

	public void learn(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<LearnInvocation> callback);
	
	public void review(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<String> asyncCallback);

	public void parse(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<ParseInvocation> callback);
	
	public void output(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<Boolean> callback);

	
	
	public void getDescription(AuthenticationToken authenticationToken, String target, AsyncCallback<String> callback);
	
	public void setDescription(AuthenticationToken authenticationToken, String target, String description, AsyncCallback<Boolean> callback);

	public void getLatestResumable(AuthenticationToken authenticationToken, AsyncCallback<MatrixGenerationConfiguration> asyncCallback);

	public void getMatrixGenerationConfiguration(AuthenticationToken authenticationToken, Task task, AsyncCallback<MatrixGenerationConfiguration> callback);
	
	public void cancel(AuthenticationToken authenticationToken, Task task, AsyncCallback<Void> callback);
	
	/*public void getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob, AsyncCallback<MatrixGenerationJobStatus> callback);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob, AsyncCallback<Void> callback);
	*/
}
