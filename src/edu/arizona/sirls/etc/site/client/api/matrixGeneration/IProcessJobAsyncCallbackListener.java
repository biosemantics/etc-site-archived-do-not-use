package edu.arizona.sirls.etc.site.client.api.matrixGeneration;

import edu.arizona.sirls.etc.site.shared.rpc.LearnInvocation;

public interface IProcessJobAsyncCallbackListener {

	public void notifyResult(LearnInvocation result);

	public void notifyException(Throwable caught);
	
}
