package edu.arizona.sirls.etc.site.shared.rpc.file.search;

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
		System.out.println("//" + element + "=[text()='" + value + "']");
		return "//" + element + "[text()='" + value + "']";
	}

}
