package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

public interface Parse extends Callable<ParseResult>, Task {

	public void destroy();
	
}
