package edu.arizona.sirls.etc.site.shared.rpc.file.search;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class SearchResult implements Serializable, Comparable<SearchResult> {

	private static final long serialVersionUID = -2060179646121834566L;
	//private int occurrences;
	private Set<String> targets;
	private String capturedMatch;

	public SearchResult() {
		
	}

	public SearchResult(String capturedMatch, Set<String> targets) {
		super();
		this.targets = targets;
		this.capturedMatch = capturedMatch;
	}


	/*public String getTarget() {
		return target;
	}

	public int getOccurrences() {
		return occurrences;
	}*/

	public Set<String> getTargets() {
		return targets;
	}


	public void setTargets(Set<String> targets) {
		this.targets = targets;
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
