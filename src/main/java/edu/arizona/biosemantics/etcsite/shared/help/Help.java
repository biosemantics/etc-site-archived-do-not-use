package edu.arizona.biosemantics.etcsite.shared.help;

import java.util.HashMap;
import java.util.Map;

public class Help {

	public static enum Type {
		WELCOME,
		SemanticMarkupInput, SemanticMarkupProcess, MatrixGenerationInput,Home; //...
		
		public String getKey() {
			return this.getClass() + "_" + this.name();
		}
	}

	public static Map<Type, String> helps = new HashMap<Type, String>();
	
	static {
		helps.put(Type.SemanticMarkupInput, 
				"here goes the help html for semanticmarkup input");

		helps.put(Type.SemanticMarkupProcess, 
				"here goes the help html for semanticmarkup process");

		helps.put(Type.MatrixGenerationInput, 
				"here goes the help html for matrix generation input");
		
		//..
	}
	public static String getHelp(Type type) {
		return helps.get(type);
		}
	
	
}
