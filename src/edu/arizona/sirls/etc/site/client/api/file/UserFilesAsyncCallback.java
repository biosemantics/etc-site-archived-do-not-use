package edu.arizona.sirls.etc.site.client.api.file;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.shared.rpc.Tree;

public class UserFilesAsyncCallback implements AsyncCallback<Tree<String>> {

	private Set<IUserFilesAsyncCallbackListener> listeners = new HashSet<IUserFilesAsyncCallbackListener>();
	
	@Override
	public void onFailure(Throwable caught) {
		for(IUserFilesAsyncCallbackListener listener : listeners) 
			listener.notifyException(caught, this);
	}

	@Override
	public void onSuccess(Tree<String> result) {
		for(IUserFilesAsyncCallbackListener listener : listeners) 
			listener.notifyResult(result, this);
	}

	public void addListener(IUserFilesAsyncCallbackListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IUserFilesAsyncCallbackListener listener) {
		this.listeners.remove(listener);
	}

}
