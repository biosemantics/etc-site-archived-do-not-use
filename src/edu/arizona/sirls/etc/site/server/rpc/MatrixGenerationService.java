package edu.arizona.sirls.etc.site.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJobStatus;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private IAuthenticationService authenticationService = new AuthenticationService();

	@Override
	public LearnInvocation learn(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			return new LearnInvocation(5989, 23212);
		}
		return null;
	}


	/*@Override
	public MatrixGenerationJobStatus getJobStatus(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			return MatrixGenerationJobStatus.MARKUP;
		}
		return null;
	}

	@Override
	public void cancelJob(AuthenticationToken authenticationToken, MatrixGenerationJob matrixGenerationJob) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			
		}
		//TODO
	}*/

}
