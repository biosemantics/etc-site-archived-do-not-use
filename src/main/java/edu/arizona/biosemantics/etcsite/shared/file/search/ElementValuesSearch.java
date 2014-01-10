package edu.arizona.biosemantics.etcsite.shared.file.search;

public class ElementValuesSearch implements Search {

	private static final long serialVersionUID = -7085508762026653893L;
	private String element;
	private String value;

	public ElementValuesSearch() { }
	
	public ElementValuesSearch(String element, String value) {
		this.element = element;
		this.value = value;
	}
	
	@Override
	public String getXPath() {
		if(value.isEmpty())
			return "//" + element + "[text()[matches(., '^.*$')]]";
		return "//" + element + "[text()='" + value + "']";
	}

}
