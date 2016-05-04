package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

public interface Enhance extends Callable<Void>, Task {

	public void destroy();
	
}
