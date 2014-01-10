package edu.arizona.biosemantics.etcsite.shared.file.search;

import java.io.Serializable;
import java.util.Set;

public class SearchResult implements Serializable, Comparable<SearchResult> {

	private static final long serialVersionUID = -2060179646121834566L;
	//private int occurrences;
	private Set<String> filePaths;
	private String capturedMatch;

	public SearchResult() {
		
	}

	public SearchResult(String capturedMatch, Set<String> filePaths) {
		super();
		this.filePaths = filePaths;
		this.capturedMatch = capturedMatch;
	}


	/*public String getTarget() {
		return target;
	}

	public int getOccurrences() {
		return occurrences;
	}*/

	public Set<String> getFilePaths() {
		return filePaths;
	}


	public void setTargets(Set<String> filePaths) {
		this.filePaths = filePaths;
	}


	public String getCapturedMatch() {
		return capturedMatch;
	}


	public void setCapturedMatch(String capturedMatch) {
		this.capturedMatch = capturedMatch;
	}


	@Override
	public int compareTo(SearchResult searchResult) {
		return this.getCapturedMatch().compareTo(searchResult.getCapturedMatch());
		//return searchResult.occurrences - this.occurrences;
	}
	
	

}
