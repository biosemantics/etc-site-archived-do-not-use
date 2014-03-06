package edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup;

public class TaxonIdentificationEntry {

	private String rank;
	private String value;
	
	public TaxonIdentificationEntry(String rank, String value) {
		super();
		this.rank = rank;
		this.value = value;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
