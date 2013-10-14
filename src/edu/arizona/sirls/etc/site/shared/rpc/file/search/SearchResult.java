package edu.arizona.sirls.etc.site.shared.rpc.file.search;

import java.io.Serializable;

public class SearchResult implements Serializable, Comparable<SearchResult> {

	private static final long serialVersionUID = -2060179646121834566L;
	private int occurrences;
	private String target;

	public SearchResult() {
		
	}
	
	public SearchResult(String target, int occurrences) {
		super();
		this.occurrences = occurrences;
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public int getOccurrences() {
		return occurrences;
	}

	@Override
	public int compareTo(SearchResult searchResult) {
		return searchResult.occurrences - this.occurrences;
	}

}
