package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SemanticMarkupConfiguration extends AbstractTaskConfiguration implements Serializable {

	private static final long serialVersionUID = -2317947666906682233L;
	private String input;
	private int numberOfInputFiles;
	private TaxonGroup taxonGroup;
	private boolean useEmptyGlossary;
	private int otoUploadId;
	private String otoSecret;
	private boolean otoCreatedDataset;
	private String output;
	private String outputTermReview;
	
	public SemanticMarkupConfiguration() { }
	
	public SemanticMarkupConfiguration(Configuration configuration, String input, int numberOfInputFiles, TaxonGroup taxonGroup, 
			boolean useEmptyGlossary, int otoUploadId, String otoSecret, boolean otoCreatedDataset, String output, String outputTermReview) {
		super(configuration);
		this.input = input;
		this.numberOfInputFiles = numberOfInputFiles;
		this.taxonGroup = taxonGroup;
		this.useEmptyGlossary = useEmptyGlossary;
		this.otoUploadId = otoUploadId;
		this.otoSecret = otoSecret;
		this.otoCreatedDataset = otoCreatedDataset;
		this.output = output;
		this.outputTermReview = outputTermReview;
	}

	public void setUseEmptyGlossary(boolean useEmptyGlossary) {
		this.useEmptyGlossary = useEmptyGlossary;
	}
	
	public boolean isUseEmptyGlossary() {
		return useEmptyGlossary;
	}

	public String getInput() {
		return input;
	}
	
	public boolean hasInput() {
		return input != null && !input.isEmpty();
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

	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}
	
	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
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
	
	public boolean isOtoCreatedDataset() {
		return otoCreatedDataset;
	}

	public void setOtoCreatedDataset(boolean otoCreatedDataset) {
		this.otoCreatedDataset = otoCreatedDataset;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public List<String> getInputs() {
		List<String> result = new LinkedList<String>();
		if(hasInput())
			result.add(getInput());
		return result;
	}

	@Override
	public List<String> getOutputs() {
		List<String> result = new LinkedList<String>();
		if(hasOutput())
			result.add(getOutput());
		if(hasOutputTermReview())
			result.add(getOutputTermReview());
		return result;
	}

	private boolean hasOutputTermReview() {
		return outputTermReview != null && !outputTermReview.isEmpty();
	}

	private boolean hasOutput() {
		return output != null && !output.isEmpty();
	}

	public String getOutputTermReview() {
		return this.outputTermReview;
	}
	
	public void setOutputTermReview(String outputTermReview) {
		this.outputTermReview = outputTermReview;
	}

}
