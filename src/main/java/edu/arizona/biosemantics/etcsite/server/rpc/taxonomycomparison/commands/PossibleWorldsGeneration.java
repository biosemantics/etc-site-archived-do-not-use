package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

public interface PossibleWorldsGeneration extends Callable<Void>, Task {

	public void destroy();
	
}
