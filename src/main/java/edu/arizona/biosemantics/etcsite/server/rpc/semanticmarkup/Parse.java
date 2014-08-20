package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.util.concurrent.Callable;

public interface Parse extends Callable<ParseResult> {

	public void destroy();
	
}
