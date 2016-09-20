package edu.arizona.biosemantics.etcsite.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ParallelRPCCallback<T> implements AsyncCallback<T> {

    private T data;
    protected ParentRPCCallback parent;
	private boolean failed;

    public T getData() {
        return data;
    }

    protected void setParent(ParentRPCCallback parentCallback) {
        this.parent = parentCallback;
    }

	@Override
	public void onSuccess(T result) {
		this.data = result;
        parent.done();
	}
	
	@Override
	public void onFailure(Throwable caught) {
		setFailure();
		//super.onFailure(caught);
	}
	
	public void setFailure() {
		failed = true;
		parent.done();
	}
	
	public boolean getFailure(){
		return failed;
	}
}