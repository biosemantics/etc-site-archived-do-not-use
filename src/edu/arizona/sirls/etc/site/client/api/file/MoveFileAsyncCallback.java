package edu.arizona.sirls.etc.site.client.api.file;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MoveFileAsyncCallback implements AsyncCallback<Boolean> {

	private Set<IMoveFileAsyncCallbackListener> listeners = new HashSet<IMoveFileAsyncCallbackListener>();
	
	public void addListener(IMoveFileAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IMoveFileAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onSuccess(Boolean result) {
		for(IMoveFileAsyncCallbackListener listener : listeners) {
			listener.notifyResult(result);
		}
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(IMoveFileAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}

}
