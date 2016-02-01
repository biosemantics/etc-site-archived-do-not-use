package edu.arizona.biosemantics.etcsite.core.shared.model.taxonomycomparison;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.etcsite.core.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.core.shared.model.Configuration;

public class TaxonomyComparisonConfiguration extends AbstractTaskConfiguration implements Serializable {

	private String cleanTaxInput;
	private String modelInput1;
	private String modelInput2;
	private String output;
	
	public TaxonomyComparisonConfiguration() { }
	

	public TaxonomyComparisonConfiguration(Configuration configuration,
			String cleanTaxInput, String modelInput1, String modelInput2, String output) {
		super(configuration);
		this.cleanTaxInput = cleanTaxInput;
		this.modelInput1 = modelInput1;
		this.modelInput2 = modelInput2;
		this.output = output;
	}

	@Override
	public List<String> getInputs() {
		List<String> result = new LinkedList<String>();
		if(this.hasCleanTaxInput())
			result.add(this.getCleanTaxInput());
		if(this.hasModelInputs()) {
			result.add(this.getModelInput1());
			result.add(this.getModelInput2());
		}
		return result;
	}

	@Override
	public List<String> getOutputs() {
		List<String> result = new LinkedList<String>();
		result.add(this.getOutput());
		return result;
	}
	
	public boolean hasCleanTaxInput() {
		return cleanTaxInput != null && !cleanTaxInput.isEmpty();
	}
	
	public boolean hasModelInputs() {
		return this.modelInput1 != null && this.modelInput2 != null && 
				!this.modelInput1.isEmpty() && !this.modelInput2.isEmpty();
	}

	public String getCleanTaxInput() {
		return cleanTaxInput;
	}

	public void setCleanTaxInput(String cleanTaxInput) {
		this.cleanTaxInput = cleanTaxInput;
	}
	
	public String getModelInput1() {
		return modelInput1;
	}

	public void setModelInput1(String modelInput1) {
		this.modelInput1 = modelInput1;
	}

	public String getModelInput2() {
		return modelInput2;
	}


	public void setModelInput2(String modelInput2) {
		this.modelInput2 = modelInput2;
	}


	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
