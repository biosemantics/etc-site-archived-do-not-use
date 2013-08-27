package edu.arizona.sirls.etc.site.client.api.matrixGeneration;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.LearnInvocation;

public class ProcessJobAsyncCallback implements AsyncCallback<LearnInvocation> {

	private Set<IProcessJobAsyncCallbackListener> listeners = new HashSet<IProcessJobAsyncCallbackListener>();
	
	public void addListener(IProcessJobAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IProcessJobAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onSuccess(LearnInvocation result) {
		for(IProcessJobAsyncCallbackListener listener : listeners) {
			listener.notifyResult(result);
		}
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(IProcessJobAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}
	
}
