package edu.arizona.sirls.etc.site.client.api.file;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GetFileContentAsyncCallback implements AsyncCallback<String> {

	private Set<IGetFileContentAsyncCallbackListener> listeners = new HashSet<IGetFileContentAsyncCallbackListener>();
	
	@Override
	public void onFailure(Throwable caught) {
		for(IGetFileContentAsyncCallbackListener listener : listeners)
			listener.notifyException(caught);
	}

	@Override
	public void onSuccess(String result) {
		for(IGetFileContentAsyncCallbackListener listener : listeners)
			listener.notifyResult(result);
	}
	
	
	public void addListener(IGetFileContentAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IGetFileContentAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

}
