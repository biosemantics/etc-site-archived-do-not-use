package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SemanticMarkupConfiguration extends AbstractTaskConfiguration implements Serializable {

	private static final long serialVersionUID = -2317947666906682233L;
	private String input;
	private int numberOfInputFiles;
	private Glossary glossary;
	private int otoUploadId;
	private String otoSecret;
	private String output;
	
	public SemanticMarkupConfiguration() { }
	
	public SemanticMarkupConfiguration(Configuration configuration, String input, int numberOfInputFiles, Glossary glossary, int otoUploadId, String otoSecret, String output) {
		super(configuration);
		this.input = input;
		this.numberOfInputFiles = numberOfInputFiles;
		this.glossary = glossary;
		this.otoUploadId = otoUploadId;
		this.otoSecret = otoSecret;
		this.output = output;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public int getNumberOfInputFiles() {
		return numberOfInputFiles;
	}

	public void setNumberOfInputFiles(int numberOfInputFiles) {
		this.numberOfInputFiles = numberOfInputFiles;
	}

	public Glossary getGlossary() {
		return glossary;
	}

	public void setGlossary(Glossary glossary) {
		this.glossary = glossary;
	}

	public int getOtoUploadId() {
		return otoUploadId;
	}

	public void setOtoUploadId(int otoUploadId) {
		this.otoUploadId = otoUploadId;
	}

	public String getOutput() {
		return output;
	}
	
	public String getOtoSecret() {
		return this.otoSecret;
	}
	
	public void setOtoSecret(String otoSecret) {
		this.otoSecret = otoSecret;
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
