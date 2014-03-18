package edu.arizona.biosemantics.etcsite.shared.rpc;

public class ParallelRPCCallback<T> extends RPCCallback<T> {

    private T data;
    private ParentRPCCallback parent;

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
}