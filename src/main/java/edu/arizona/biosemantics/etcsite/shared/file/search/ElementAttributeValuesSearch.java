package edu.arizona.biosemantics.etcsite.shared.file.search;

public class ElementAttributeValuesSearch implements Search {

	private static final long serialVersionUID = -6514275279774904321L;
	private String element;
	private String attribute;
	private String value;

	public ElementAttributeValuesSearch() { }
	
	public ElementAttributeValuesSearch(String element, String attribute, String value) {
		this.element = element;
		this.attribute = attribute;
		this.value = value;
	}
	
	@Override
	public String getXPath() {
		if(value.isEmpty())
			return "//" + element + "[@" + attribute + "[matches(., '^.*$')]]";
		return "//" + element + "[@" + attribute + "='" + value + "']";
	}

	public String getAttribute() {
		return attribute;
	}

}
