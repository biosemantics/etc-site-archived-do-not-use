package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.client.common.MessageView;

public abstract class RPCCallback<T> implements AsyncCallback<RPCResult<T>> {

	private LoadingPopup loadingPopup;
	//private MessageView.Presenter messagePresenter = new MessagePresenter(new MessageView());
	
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
			//messagePresenter.showMessage("Error", result.getMessage());
			System.out.println("rpc call failed on the server with message: " + result.getMessage());
		}
		if(loadingPopup != null)
			loadingPopup.stop();
	}
	
	public abstract void onResult(T result);

}
