package edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup;

import java.io.Serializable;

public class Description implements Serializable {

	private String content;
	private String type;
	
	public Description() { }
	
	public Description(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
