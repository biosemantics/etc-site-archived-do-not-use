package edu.arizona.sirls.etc.site.shared.rpc;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.TaxonomyComparisonConfiguration;

public class TaxonomyComparisonTaskRun extends AbstractTaskRun<TaxonomyComparisonConfiguration> {

	private static final long serialVersionUID = -8582247193964890727L;
	private TaxonomyComparisonConfiguration configuration;

	public TaxonomyComparisonTaskRun() { }
	
	public TaxonomyComparisonTaskRun(Task task, TaxonomyComparisonConfiguration configuration) {
		super(task);
		this.configuration = configuration;
	}

	@Override
	public TaxonomyComparisonConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(TaxonomyComparisonConfiguration configuration) {
		this.configuration = configuration;
	}
	

}
