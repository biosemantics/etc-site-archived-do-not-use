package edu.arizona.biosemantics.etcsite.core.shared.model.matrixgeneration;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.etcsite.core.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.core.shared.model.Configuration;
import edu.arizona.biosemantics.etcsite.core.shared.model.TaxonGroup;

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
		result.add(this.getInput());
		result.add(this.getInputOntology());
		result.add(this.getInputTermReview());
		return result;
	}

	@Override
	public List<String> getOutputs() {
		List<String> result = new LinkedList<String>();
		result.add(this.getOutput());
		return result;
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
