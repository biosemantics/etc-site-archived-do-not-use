package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;
import java.util.HashSet;

public class ParseDummy implements IParse {
	@Override
	public ParseResult call() throws Exception {
		Thread.sleep(10000);
		return new ParseResult(new HashSet<File>());
	}

}
