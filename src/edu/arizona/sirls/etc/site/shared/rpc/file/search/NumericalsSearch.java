package edu.arizona.sirls.etc.site.shared.rpc.file.search;

public class NumericalsSearch implements Search {
	
	private static final long serialVersionUID = 989168390667647930L;

	public NumericalsSearch() { }
	
	@Override
	public String getXPath() {
		return "//*[@*[matches(., '^[\\c|\\d]+$')]]";
	}

}
