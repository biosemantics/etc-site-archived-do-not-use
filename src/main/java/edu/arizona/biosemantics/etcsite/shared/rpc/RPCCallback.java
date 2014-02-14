package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;

public abstract class RPCCallback<T> implements AsyncCallback<RPCResult<T>> {

	private LoadingPopup loadingPopup;

	public RPCCallback() { }
	
	public RPCCallback(LoadingPopup loadingPopup) {
		this.loadingPopup = loadingPopup;
	}

	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		if(loadingPopup != null)
			loadingPopup.stop();
	}

	@Override
	public void onSuccess(RPCResult<T> result) {
		if(result.isSucceeded()) {
			this.onResult(result.getData());
		} else {
			System.out.println("rpc call failed on the server with message: " + result.getMessage());
		}
		if(loadingPopup != null)
			loadingPopup.stop();
	}
	
	public abstract void onResult(T result);

}
