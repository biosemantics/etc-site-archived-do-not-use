package edu.arizona.sirls.etc.site.client.api.file;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SetFileContentAsyncCallback implements AsyncCallback<Boolean> {

	private Set<ISetFileContentAsyncCallbackListener> listeners = new HashSet<ISetFileContentAsyncCallbackListener>();
	
	@Override
	public void onFailure(Throwable caught) {
		for(ISetFileContentAsyncCallbackListener listener : listeners)
			listener.notifyException(caught);
	}

	@Override
	public void onSuccess(Boolean result) {
		for(ISetFileContentAsyncCallbackListener listener : listeners)
			listener.notifyResult(result);
	}
	
	
	public void addListener(ISetFileContentAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ISetFileContentAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

}
