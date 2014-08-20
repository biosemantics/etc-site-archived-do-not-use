package edu.arizona.biosemantics.etcsite.shared.model.file;

import java.io.Serializable;

public class TaxonIdentificationEntry implements Serializable {

	private String rank;
	private String value;
	
	public TaxonIdentificationEntry() { }
	
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
