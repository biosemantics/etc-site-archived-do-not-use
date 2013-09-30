package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.PreprocessedDescription;

@RemoteServiceRelativePath("matrixGeneration")
public interface IMatrixGenerationService extends RemoteService {

	public MatrixGenerationConfiguration start(AuthenticationToken authenticationToken, String taskName, 
			String input, String glossaryName);
	
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);
	
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);

	public String review(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);
	
	public ParseInvocation parse(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);
	
	public boolean output(AuthenticationToken authenticationToken, MatrixGenerationConfiguration matrixGenerationConfiguration);

	public String getDescription(AuthenticationToken authenticationToken, String target);
	
	public boolean setDescription(AuthenticationToken authenticationToken, String target, String description);
	
	public MatrixGenerationConfiguration getLatestResumable(AuthenticationToken authenticationToken);
	
	public MatrixGenerationConfiguration getMatrixGenerationConfiguration(AuthenticationToken authenticationToken, Task task);
	
	public void cancel(AuthenticationToken authenticationToken, Task task);
	
	/*public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob);
	
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob); */
}
