package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public interface IMatrixGenerationServiceAsync {

	public void preprocess(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<List<PreprocessedDescription>> callback);
	
	public void getDescription(AuthenticationToken authenticationToken, String target, AsyncCallback<String> callback);
	
	public void setDescription(AuthenticationToken authenticationToken, String target, String description, AsyncCallback<Boolean> callback);
	
	public void learn(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<LearnInvocation> callback);

	public void outputResult(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration, AsyncCallback<Boolean> callback);

	
	/*public void getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob, AsyncCallback<MatrixGenerationJobStatus> callback);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob, AsyncCallback<Void> callback);
	*/
}
