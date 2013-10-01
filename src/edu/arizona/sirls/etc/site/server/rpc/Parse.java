package edu.arizona.sirls.etc.site.server.rpc;

import java.util.concurrent.Callable;

public class Parse implements Callable<ParseResult> {
	@Override
	public ParseResult call() throws Exception {
		Thread.sleep(10000);
		return new ParseResult();
	}

}
