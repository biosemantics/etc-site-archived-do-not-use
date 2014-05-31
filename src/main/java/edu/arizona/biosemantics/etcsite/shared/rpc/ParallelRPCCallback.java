package edu.arizona.biosemantics.etcsite.shared.rpc;

public class ParallelRPCCallback<T> extends RPCCallback<T> {

    private T data;
    private ParentRPCCallback parent;
	private boolean failed;

    public T getData() {
        return data;
    }

    protected void setParent(ParentRPCCallback parentCallback) {
        this.parent = parentCallback;
    }

	@Override
	public void onResult(T result) {
		this.data = result;
        parent.done();
	}
	
	public void setFailure() {
		failed = true;
		parent.done();
	}
	
	public boolean getFailure(){
		return failed;
	}
}