package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

public interface MIRGeneration extends Callable<Void>, Task {

	public void destroy();
	
}
