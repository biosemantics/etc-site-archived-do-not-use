package edu.arizona.sirls.etc.site.client.api.fileFormat;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ValidTaxonDescriptionAsyncCallback implements AsyncCallback<Boolean> {

	private Set<IValidFileAsyncCallbackListener> listeners = new HashSet<IValidFileAsyncCallbackListener>();
	
	public void addListener(IValidFileAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IValidFileAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onSuccess(Boolean result) {
		for(IValidFileAsyncCallbackListener listener : listeners) {
			listener.notifyResult(result);
		}
	}
	
	@Override
	public void onFailure(Throwable caught) {
		for(IValidFileAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught);
	}

}

