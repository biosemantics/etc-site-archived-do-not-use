package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	public String getDescription(AuthenticationToken authenticationToken, String target);
	
	public boolean setDescription(AuthenticationToken authenticationToken, String target, String description);
	
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);

	public boolean outputResult(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	/*public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob); */
}
