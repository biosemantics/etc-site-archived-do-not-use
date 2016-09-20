package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

public interface ConsistencyCheck extends Callable<Boolean>, Task {

	public void destroy();

}
