package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OntologizeConfiguration  extends AbstractTaskConfiguration implements Serializable {

	private String input;
	private TaxonGroup taxonGroup;
	private String ontologyFile;
	private String output;
	private int ontologizeCollectionId;
	private String ontologizeCollectionSecret;
	
	public OntologizeConfiguration() { }
	
	public OntologizeConfiguration(Configuration configuration, String input, TaxonGroup taxonGroup,
			String ontologyFile, int ontologizeCollectionId, String ontologizeCollectionSecret, String output) {
		super(configuration);
		this.input = input;
		this.taxonGroup = taxonGroup;
		this.ontologyFile = ontologyFile;
		this.ontologizeCollectionId = ontologizeCollectionId;
		this.ontologizeCollectionSecret = ontologizeCollectionSecret;
		this.output = output;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}

	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
	}

	public String getOntologyFile() {
		return ontologyFile;
	}

	public void setOntologyFile(String ontologyFile) {
		this.ontologyFile = ontologyFile;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public List<String> getInputs() {
		List<String> inputs = new LinkedList<String>();
		if(hasInput())
			inputs.add(input);
		return inputs;
	}

	private boolean hasInput() {
		return input != null && !input.isEmpty();
	}

	@Override
	public List<String> getOutputs() {
		return new LinkedList<String>();
	}

	public int getOntologizeCollectionId() {
		return ontologizeCollectionId;
	}

	public String getOntologizeCollectionSecret() {
		return ontologizeCollectionSecret;
	}

	public void setOntologizeCollectionId(int ontologizeCollectionId) {
		this.ontologizeCollectionId = ontologizeCollectionId;
	}

	public void setOntologizeCollectionSecret(String ontologizeCollectionSecret) {
		this.ontologizeCollectionSecret = ontologizeCollectionSecret;
	}
	
	
}
