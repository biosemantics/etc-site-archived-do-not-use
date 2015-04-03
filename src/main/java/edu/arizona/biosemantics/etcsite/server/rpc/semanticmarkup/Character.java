package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

public class Character {

	private String value;
	private String category;
	
	public Character(String value, String category) {
		this.value = value;
		this.category = category;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		return this.category + ": " + this.value;
	}
	
	

}
