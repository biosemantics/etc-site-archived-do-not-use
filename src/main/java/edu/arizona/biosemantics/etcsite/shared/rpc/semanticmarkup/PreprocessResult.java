package edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup;

import java.util.List;

public class PreprocessResult {

	private List<PreprocessedDescription> preprocessedDescriptions;
	
	public PreprocessResult(List<PreprocessedDescription> preprocessedDescriptions) {
		this.preprocessedDescriptions = preprocessedDescriptions;
	}

	public List<PreprocessedDescription> getPreprocessedDescriptions() {
		return preprocessedDescriptions;
	}

	public void setPreprocessedDescriptions(
			List<PreprocessedDescription> preprocessedDescriptions) {
		this.preprocessedDescriptions = preprocessedDescriptions;
	}
	
	
}
