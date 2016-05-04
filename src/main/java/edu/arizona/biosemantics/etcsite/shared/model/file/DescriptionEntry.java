package edu.arizona.biosemantics.etcsite.shared.model.file;

import java.io.Serializable;

import edu.arizona.biosemantics.common.taxonomy.Description;

@SuppressWarnings("serial")
public class DescriptionEntry implements Serializable{


	private Description type;
	private String scope;
	private String description;


	public DescriptionEntry(Description type,String scope, String description) {
		super();
		this.type = type;
		this.scope = scope;
		this.description = description;
	}
	
	public Description getType() {
		return type;
	}

	public void setType(Description type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	

}
