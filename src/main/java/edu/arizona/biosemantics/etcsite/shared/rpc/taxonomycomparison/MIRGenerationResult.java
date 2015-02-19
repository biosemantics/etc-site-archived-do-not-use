package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import java.io.Serializable;
import java.util.List;

public class MIRGenerationResult implements Serializable {

	public enum Type {
		CONFLICT,
		ONE,
		MULTIPLE
	}

	private Type type;
	private List<String> possibleWorldFiles;
	
	public MIRGenerationResult() { }
	
	public MIRGenerationResult(Type type) {
		this.type = type;
	}
	
	public MIRGenerationResult(Type type, List<String> possibleWorldFiles) {
		this.type = type;
		this.possibleWorldFiles = possibleWorldFiles;
	}

	public List<String> getPossibleWorldFiles() {
		return possibleWorldFiles;
	}

	public void setPossibleWorldFiles(List<String> possibleWorldFiles) {
		this.possibleWorldFiles = possibleWorldFiles;
	}

	public Type getType() {
		return type;
	}
	
	
}
