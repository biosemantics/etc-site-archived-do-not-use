package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.commands;

import java.util.concurrent.Callable;

import edu.arizona.biosemantics.etcsite.server.Task;

//Euler currently does not provide a clear indication of invalid or valid input.
//it only lists warnings on the output. Warnings can or cannot mean it is valid or invalid.
//needs clear boolean response
public interface InputFormatCheck extends Callable<Boolean>, Task {

	public void destroy();
}
