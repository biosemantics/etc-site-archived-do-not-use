package edu.arizona.biosemantics.etcsite.shared.file.search;

public class XPathSearch implements Search {

	private static final long serialVersionUID = -1528756861447282181L;
	private String xpath;

	public XPathSearch() { }
	
	public XPathSearch(String xpath) {
		this.xpath = xpath;
	}
	
	@Override
	public String getXPath() {
		return this.xpath;
	}

}
