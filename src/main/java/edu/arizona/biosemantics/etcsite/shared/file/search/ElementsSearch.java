package edu.arizona.biosemantics.etcsite.shared.file.search;

public class ElementsSearch implements Search {

	private static final long serialVersionUID = 2563974142619673145L;
	private String element;

	public ElementsSearch() { }
	
	public ElementsSearch(String element) {
		this.element = element;
	}
	
	@Override
	public String getXPath() {
		return "//" + element;
	}

}
