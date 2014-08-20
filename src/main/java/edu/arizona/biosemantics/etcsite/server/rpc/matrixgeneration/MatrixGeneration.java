package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.util.concurrent.Callable;

public interface MatrixGeneration extends Callable<Boolean> {

	public void destroy();
	
}
