package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;

public class TaxonomyComparisonException extends Exception {

	private Task task;
	
	public TaxonomyComparisonException() { }
		
	public TaxonomyComparisonException(String message) {
		super(message);
	}
	
	public TaxonomyComparisonException(Task task) {
		this.task = task;
	}
	
	public TaxonomyComparisonException(String message, Task task) {
		super(message);
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
}
