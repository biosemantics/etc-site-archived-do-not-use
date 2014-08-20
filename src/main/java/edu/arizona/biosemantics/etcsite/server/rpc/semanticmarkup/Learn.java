package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.util.concurrent.Callable;

public interface Learn extends Callable<LearnResult> {

	public void destroy();
	
}
