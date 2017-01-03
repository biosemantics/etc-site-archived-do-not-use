package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.HashSet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;

public class InJvmMicropieParse implements MicropieParse {

	private String input;
	private String output;
	private String models;
	private boolean executedSuccessfully;

	public InJvmMicropieParse(String input, String output, String models) {
		this.input = input;
		this.output = output;
		this.models = models;
	}
	
	@Override
	public ParseResult call() throws SemanticMarkupException {
		ParseResult result = new ParseResult(new HashSet<File>());
		
		String[] args = new String[] { "-i", input, "-o", output, "-m", models, "-f", "xml"};
		
		try {
			edu.arizona.biosemantics.micropie.Main.main(args);
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Semantic Markup Parse failed with exception.", e);
			executedSuccessfully = false;
			throw new SemanticMarkupException();
		}
		
		return result;
	}
	
	@Override
	public void destroy() {}

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

}

