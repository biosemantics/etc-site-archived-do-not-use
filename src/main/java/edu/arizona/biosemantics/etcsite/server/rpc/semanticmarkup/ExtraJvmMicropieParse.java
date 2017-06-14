package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;

public class ExtraJvmMicropieParse extends ExtraJvmCallable<ParseResult> implements MicropieParse {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				edu.arizona.biosemantics.micropie.Main.main(args);
			} catch (Throwable t) {
				System.exit(-1);
			}
		}
	}
	
	private String model;
	private String input;
	private String output;

	public ExtraJvmMicropieParse(String input, String output, String model) {
		super();
		this.input = input;
		this.output = output;
		this.model = model;
		
		this.setArgs(createArgs());
		if(!Configuration.charaparser_xms.isEmpty()) 
			this.setXms(Configuration.charaparser_xms);
		if(!Configuration.charaparser_xmx.isEmpty()) 
			this.setXmx(Configuration.charaparser_xmx);
		
		//could be reduced to only libraries relevant to semantic-markup
		if(Configuration.micropie_classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.micropie_classpath);
		this.setMainClass(MainWrapper.class);
	}

	private String[] createArgs() {
		List<String> argList = new LinkedList<String>();
		addArg(argList, "i", input);
		addArg(argList, "o", output);
		addArg(argList, "m", model);
		addArg(argList, "f", "xml");
		
		String[] args = argList.toArray(new String[argList.size()]);
		return args;
	}
	
	private void addArg(List<String> argList, String arg, String value) {
		if(value != null && !value.isEmpty()) {
			argList.add("-" + arg);
			argList.add(value);
		}
	}

	@Override
	public ParseResult createReturn() throws SemanticMarkupException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Micropie Markup failed.");
			throw new SemanticMarkupException();
		}
		ParseResult result = new ParseResult(new HashSet<File>());
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		/*InJvmMicropieParse parse = new InJvmMicropieParse("C:/etcsitebase/etcsite/data/users/4/smicropie_demo", 
				"C:/etcsitebase/etcsite/data/textCapture/charaparser/352/out", 
				"C:/etcsitebase/etcsite/resources/textCapture/micropie/models");
		*/
		ExtraJvmMicropieParse parse = new ExtraJvmMicropieParse("C:/etcsitebase/etcsite/data/users/4/smicropie_demo", 
		"C:/etcsitebase/etcsite/data/textCapture/charaparser/352/out", 
		"C:/etcsitebase/etcsite/resources/textCapture/micropie/models");
		parse.call();
	}

}
