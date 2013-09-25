package edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration;

import java.io.Serializable;
import java.util.Map;

public class PreprocessedDescription implements Serializable {

	private static final long serialVersionUID = -3004957797166336806L;
	private String fileName;
	private int descriptionNumber;
	private String target;
	private Map<Character, Integer> bracketCounts;

	public PreprocessedDescription() {

	}

	public PreprocessedDescription(String target, String fileName,
			int descriptionNumber, Map<Character, Integer> bracketCounts) {
		this.target = target;
		this.fileName = fileName;
		this.descriptionNumber = descriptionNumber;
		this.bracketCounts = bracketCounts;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getDescriptionNumber() {
		return descriptionNumber;
	}

	public void setDescriptionNumber(int descriptionNumber) {
		this.descriptionNumber = descriptionNumber;
	}

	public Map<Character, Integer> getBracketCounts() {
		return bracketCounts;
	}

	public void setBracketCounts(Map<Character, Integer> bracketCounts) {
		this.bracketCounts = bracketCounts;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
