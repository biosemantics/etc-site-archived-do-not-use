package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);
	
	public String getDescription(AuthenticationToken authenticationToken, String target);
	
	public boolean setDescription(AuthenticationToken authenticationToken, String target, String description);
	
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);

	public boolean outputResult(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);
	
	/*public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob); */
}
