package edu.arizona.biosemantics.etcsite.shared.file.search;

public class NumericalsSearch implements Search {
	
	private static final long serialVersionUID = 989168390667647930L;
	private String numericalExpression = "^.*[\\d]+.*$";
	
	public NumericalsSearch() { }
	
	@Override
	public String getXPath() {
		return "//*[@*[matches(., '" + numericalExpression + "')]]";
	}

	public String getNumericalExpression() {
		return numericalExpression;
	}

	public void setNumericalExpression(String numericalExpression) {
		this.numericalExpression = numericalExpression;
	}
	
	

}
