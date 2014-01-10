package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MatrixGenerationConfiguration extends AbstractTaskConfiguration implements Serializable {

	private String input;
	private String output;

	public MatrixGenerationConfiguration () { }
	
	public MatrixGenerationConfiguration(Configuration configuration, String input, String output) {
		super(configuration);
		this.input = input;
		this.output = output;
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
