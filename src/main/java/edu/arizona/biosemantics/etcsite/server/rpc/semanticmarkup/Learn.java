package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

public interface Learn extends Callable<LearnResult>, Task {

	public void destroy();
	
}
