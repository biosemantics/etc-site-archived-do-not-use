package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MatrixGenerationConfiguration extends AbstractTaskConfiguration implements Serializable {

	private String input;
	private String output;
	private boolean inheritValues;
	private boolean generateAbsentPresent;
	private TaxonGroup taxonGroup;
	private String inputOntology;
	private String inputTermReview;

	public MatrixGenerationConfiguration () { }
	
	public MatrixGenerationConfiguration(Configuration configuration, String input, String inputTermReview, String inputOntology, TaxonGroup taxonGroup, String output, boolean inheritValues, boolean generateAbsentPresent) {
		super(configuration);
		this.input = input;
		this.inputOntology = inputOntology;
		this.inputTermReview = inputTermReview;
		this.taxonGroup = taxonGroup;
		this.output = output;
		this.inheritValues = inheritValues;
		this.generateAbsentPresent = generateAbsentPresent;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setInheritValues(boolean inheritValues) {
		this.inheritValues = inheritValues;
	}

	public void setGenerateAbsentPresent(boolean generateAbsentPresent) {
		this.generateAbsentPresent = generateAbsentPresent;
	}

	public boolean isInheritValues() {
		return inheritValues;
	}

	public boolean isGenerateAbsentPresent() {
		return generateAbsentPresent;
	}
	
	@Override
	public List<String> getInputs() {
		List<String> result = new LinkedList<String>();
		if(hasInput())
			result.add(this.getInput());
		if(hasInputOntology())
			result.add(this.getInputOntology());
		if(hasInputTermReview())
			result.add(this.getInputTermReview());
		return result;
	}

	private boolean hasInputTermReview() {
		return inputTermReview != null && !inputTermReview.isEmpty();
	}

	private boolean hasInputOntology() {
		return inputOntology != null && !inputOntology.isEmpty();
	}

	private boolean hasInput() {
		return input != null && !input.isEmpty();
	}

	@Override
	public List<String> getOutputs() {
		List<String> result = new LinkedList<String>();
		if(hasOutput())
			result.add(this.getOutput());
		return result;
	}

	private boolean hasOutput() {
		return output != null && !output.isEmpty();
	}

	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
	}

	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}

	public void setInputOntology(String inputOntology) {
		this.inputOntology = inputOntology;
	}
	
	public String getInputOntology() {
		return inputOntology;
	}

	public void setInputTermReview(String inputTermReview) {
		this.inputTermReview = inputTermReview;
	}
	
	public String getInputTermReview() {
		return inputTermReview;
	}
		
}
