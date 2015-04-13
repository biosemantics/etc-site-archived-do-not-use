package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TreeGenerationConfiguration extends AbstractTaskConfiguration implements Serializable {

	private String input;
	
	public TreeGenerationConfiguration() { }
	
	public TreeGenerationConfiguration(Configuration configuration, String input) {
		super(configuration);
		this.input = input;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	@Override
	public List<String> getInputs() {
		List<String> inputs = new LinkedList<String>();
		inputs.add(input);
		return inputs;
	}

	@Override
	public List<String> getOutputs() {
		return new LinkedList<String>();
	}

}
