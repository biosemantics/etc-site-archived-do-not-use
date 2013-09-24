package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.Serializable;
import java.util.List;

public class MatrixGenerationJob implements Serializable {

	private static final long serialVersionUID = 7710877104139321045L;
	
	private String taxonDescriptionFile = "";
	private String taxonGlossaryFile = "";
	private String preprocessedFile = "";
	private String reviewTermsLink = "";
	private String outputFile = "";
	private List<PreprocessedDescription> preprocessedDescriptions;	
	
	public void reset() { 
		taxonDescriptionFile = "";
		taxonGlossaryFile = "";
		preprocessedFile = "";
		reviewTermsLink = "";
		outputFile = "";
	}
	
	public String getTaxonDescriptionFile() {
		return taxonDescriptionFile;
	}
	public void setTaxonDescriptionFile(String taxonDescriptionFile) {
		this.taxonDescriptionFile = taxonDescriptionFile;
	}
	public String getTaxonGlossaryFile() {
		return taxonGlossaryFile;
	}
	public void setTaxonGlossaryFile(String taxonGlossaryFile) {
		this.taxonGlossaryFile = taxonGlossaryFile;
	}
	public String getPreprocessedFile() {
		return preprocessedFile;
	}
	public void setPreprocessedFile(String preprocessedFile) {
		this.preprocessedFile = preprocessedFile;
	}
	public String getReviewTermsLink() {
		return reviewTermsLink;
	}
	public void setReviewTermsLink(String reviewTermsLink) {
		this.reviewTermsLink = reviewTermsLink;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public boolean hasTaxonDescriptionFile() {
		return this.taxonDescriptionFile != null && !this.taxonDescriptionFile.isEmpty();
	}

	public boolean hasPreprocessedFile() {
		return this.preprocessedFile != null && !this.preprocessedFile.isEmpty();
	}
	
	public boolean hasReviewTermsLink() {
		return this.reviewTermsLink!= null && !this.reviewTermsLink.isEmpty();
	}
	
	public boolean hasOutputFile() {
		return this.outputFile != null && !this.outputFile.isEmpty();
	}

	public boolean hasTaxonGlossaryFile() {
		return this.taxonGlossaryFile != null &&  !this.taxonGlossaryFile.isEmpty();
	}

	public void setPreprocessedDescriptions(List<PreprocessedDescription> preprocessedDescriptions) {
		this.preprocessedDescriptions = preprocessedDescriptions;
	}

	public List<PreprocessedDescription> getPreprocessedDescriptions() {
		return preprocessedDescriptions;
	}
	
}
