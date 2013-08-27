package edu.arizona.sirls.etc.site.client.api.file;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DeleteFileAsyncCallback implements AsyncCallback<Boolean> {

	private Set<IDeleteFileAsyncCallbackListener> listeners = new HashSet<IDeleteFileAsyncCallbackListener>();
	
	public void addListener(IDeleteFileAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IDeleteFileAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onSuccess(Boolean result) {
		for(IDeleteFileAsyncCallbackListener listener : listeners) {
			listener.notifyResult(result);
		}
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(IDeleteFileAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}

}
