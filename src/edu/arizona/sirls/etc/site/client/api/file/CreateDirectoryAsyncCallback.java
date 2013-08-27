package edu.arizona.sirls.etc.site.client.api.file;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class CreateDirectoryAsyncCallback implements AsyncCallback<Boolean> {

	private Set<ICreateDirectoryAsyncCallbackListener> listeners = new HashSet<ICreateDirectoryAsyncCallbackListener>();
	
	public void addListener(ICreateDirectoryAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ICreateDirectoryAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onSuccess(Boolean result) {
		for(ICreateDirectoryAsyncCallbackListener listener : listeners) {
			listener.notifyResult(result);
		}
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(ICreateDirectoryAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}

}
