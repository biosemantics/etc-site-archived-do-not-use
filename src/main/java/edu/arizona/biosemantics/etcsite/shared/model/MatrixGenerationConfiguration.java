package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MatrixGenerationConfiguration extends AbstractTaskConfiguration implements Serializable {

	private String input;
	private String output;
	private boolean inheritValues;
	private boolean generateAbsentPresent;

	public MatrixGenerationConfiguration () { }
	
	public MatrixGenerationConfiguration(Configuration configuration, String input, String output, boolean inheritValues, boolean generateAbsentPresent) {
		super(configuration);
		this.input = input;
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
		return result;
	}

	@Override
	public List<String> getOutputs() {
		List<String> result = new LinkedList<String>();
		result.add(this.getOutput());
		return result;
	}
	
}
