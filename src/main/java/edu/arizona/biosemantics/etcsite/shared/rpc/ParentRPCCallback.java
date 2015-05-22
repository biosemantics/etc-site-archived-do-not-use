package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

public abstract class ParentRPCCallback {

    private int doneCount = 0;

    private List<ParallelRPCCallback> childCallbacks;

    protected ParentRPCCallback(List<ParallelRPCCallback> callbacks) {
        if (callbacks == null) {
            throw new RuntimeException("No callbacks passed to parent");
        }

        this.childCallbacks = callbacks;

        for (ParallelRPCCallback callback : callbacks) {
            callback.setParent(this);
        }
    }
    
    public int getDoneCount() {
    	return doneCount;
    }
    
	public double getCount() {
		return childCallbacks.size();
	}

    protected synchronized void done() {
        doneCount++;

        if (doneCount == childCallbacks.size()) {
        	boolean allIsWell = true;
        	for(ParallelRPCCallback parallelRPCCallback : childCallbacks) {
        		if(parallelRPCCallback.getFailure()){
        			allIsWell = false;
        			break;
        		}
        	}
            handleSuccess();
            if(!allIsWell)
            	handleFailure();
        }
    }

    protected abstract void handleFailure();
    protected abstract void handleSuccess();

    protected <D extends Object> D getCallbackData(int index) {
        if (index < 0 || index >= childCallbacks.size()) {
            throw new RuntimeException("Invalid child callback index");
        }
        return (D) childCallbacks.get(index).getData();
    }

    protected boolean getCallbackFailureState(int index) {
        if (index < 0 || index >= childCallbacks.size()) {
            throw new RuntimeException("Invalid child callback index");
        }
        return childCallbacks.get(index).getFailure();
    }
    
    protected List<ParallelRPCCallback> getChildCallbacks(){
    	return this.childCallbacks;
    }


}
