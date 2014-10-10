package edu.arizona.biosemantics.etcsite.server;

public interface Task {

	public static interface FailHandler {
		public void onFail();
	}
	
	public void addFailHandler(FailHandler handler);
	
	public void removeFailHandler(FailHandler handler);
	
	public boolean isExecutedSuccessfully();
	
}
