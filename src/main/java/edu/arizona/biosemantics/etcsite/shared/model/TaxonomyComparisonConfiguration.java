package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TaxonomyComparisonConfiguration extends AbstractTaskConfiguration implements Serializable {

	private TaxonGroup taxonGroup;
	private String cleanTaxInput;
	private String modelInput1;
	private String modelInput2;
	private String model1Author;
	private String model1Year;
	private String model2Author;
	private String model2Year;
	private String termReview1;
	private String termReview2;
	private String ontology;
	private String output;
	private int alignmentId;
	private String alignmentSecret;
	
	public TaxonomyComparisonConfiguration() { }
	

	public TaxonomyComparisonConfiguration(Configuration configuration, TaxonGroup taxonGroup,
			String cleanTaxInput, String modelInput1, String modelInput2, 
			String modelAuthor1, String modelAuthor2, String modelYear1, String modelYear2,
			String termReview1, String termReview2, 
			String ontology, String output, int alignmentId, String alignmentSecret) {
		super(configuration);
		this.taxonGroup = taxonGroup;
		this.cleanTaxInput = cleanTaxInput;
		this.modelInput1 = modelInput1;
		this.modelInput2 = modelInput2;
		this.model1Author = modelAuthor1;
		this.model2Author = modelAuthor2;
		this.model1Year = modelYear1;
		this.model2Year = modelYear2;
		this.termReview1 = termReview1;
		this.termReview2 = termReview2;
		this.ontology = ontology;
		this.output = output;
		this.alignmentId = alignmentId;
		this.alignmentSecret = alignmentSecret;
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
		if(this.hasTermReview1())
			result.add(termReview1);
		if(this.hasTermReview2())
			result.add(termReview2);
		if(this.hasOntology())
			result.add(ontology);
		return result;
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

	public boolean hasCleanTaxInput() {
		return cleanTaxInput != null && !cleanTaxInput.isEmpty();
	}
	
	private boolean hasOntology() {
		return ontology != null && !ontology.isEmpty();
	}


	private boolean hasTermReview2() {
		return termReview2 != null && !termReview2.isEmpty();
	}

	private boolean hasTermReview1() {
		return termReview1 != null && !termReview1.isEmpty();
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


	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}


	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
	}


	public String getTermReview1() {
		return termReview1;
	}


	public void setTermReview1(String termReview1) {
		this.termReview1 = termReview1;
	}


	public String getTermReview2() {
		return termReview2;
	}


	public void setTermReview2(String termReview2) {
		this.termReview2 = termReview2;
	}


	public String getOntology() {
		return ontology;
	}


	public void setOntology(String ontology) {
		this.ontology = ontology;
	}


	public int getAlignmentId() {
		return alignmentId;
	}


	public void setAlignmentId(int alignmentId) {
		this.alignmentId = alignmentId;
	}


	public String getAlignmentSecret() {
		return alignmentSecret;
	}


	public void setAlignmentSecret(String alignmentSecret) {
		this.alignmentSecret = alignmentSecret;
	}


	public String getModel1Author() {
		return model1Author;
	}


	public void setModel1Author(String model1Author) {
		this.model1Author = model1Author;
	}


	public String getModel1Year() {
		return model1Year;
	}


	public void setModel1Year(String model1Year) {
		this.model1Year = model1Year;
	}


	public String getModel2Author() {
		return model2Author;
	}


	public void setModel2Author(String model2Author) {
		this.model2Author = model2Author;
	}


	public String getModel2Year() {
		return model2Year;
	}


	public void setModel2Year(String mdoel2Year) {
		this.model2Year = mdoel2Year;
	}
}

