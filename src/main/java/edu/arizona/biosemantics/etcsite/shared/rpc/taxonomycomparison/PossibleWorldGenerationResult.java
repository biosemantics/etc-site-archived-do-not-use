package edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison;

import java.io.Serializable;

import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;

public class PossibleWorldGenerationResult implements Serializable {

	private RunOutput runOutput;
	private boolean tooLong = false;
	
	public PossibleWorldGenerationResult() { }
	
	public PossibleWorldGenerationResult(boolean tooLong, RunOutput runOutput) {
		this.runOutput = runOutput;
		this.tooLong = tooLong;
	}

	public RunOutput getRunOutput() {
		return runOutput;
	}

	public void setRunOutput(RunOutput runOutput) {
		this.runOutput = runOutput;
	}

	public boolean isTooLong() {
		return tooLong;
	}

	public void setTooLong(boolean tooLong) {
		this.tooLong = tooLong;
	}
	
	
}
