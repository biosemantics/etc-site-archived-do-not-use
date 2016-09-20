package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.HasTaskException;

public class TaxonomyComparisonException extends HasTaskException {
	
	public TaxonomyComparisonException() { }
	
	public TaxonomyComparisonException(Task task) {
		super(task);
	}
	
	public TaxonomyComparisonException(String message) {
		super(message);
	}
		
	public TaxonomyComparisonException(String message, Task task) {
		super(message, task);
	}
}
