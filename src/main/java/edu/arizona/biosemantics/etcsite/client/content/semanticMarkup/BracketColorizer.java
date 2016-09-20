package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.HashMap;
import java.util.Map;

public class BracketColorizer {

	private Map<Character, HTMLColor> characterReplacements = new HashMap<Character, HTMLColor>();
	
	public BracketColorizer() {
		characterReplacements.put('(', HTMLColor.RED);
		characterReplacements.put(')', HTMLColor.RED);
		characterReplacements.put('[', HTMLColor.BLUE);
		characterReplacements.put(']', HTMLColor.BLUE);
		characterReplacements.put('}', HTMLColor.GREEN);
		characterReplacements.put('{', HTMLColor.GREEN);
	}
	
	public String colorize(String string) {
		StringBuilder result = new StringBuilder();
		for(int i=0; i<string.length(); i++) {
			char currentCharacter = string.charAt(i);
			if(characterReplacements.containsKey(currentCharacter)) {
				result.append("<b><font color=\"" + characterReplacements.get(currentCharacter) + "\">");
				result.append(currentCharacter);
				result.append("</font></b>");
			} else {
				result.append(currentCharacter);
			}
		}
		
		//to reset to 'normal' color and remove bold some stuff has to be appended, like below.
		//using the getFormatter on RichTextArea and toggling off bold and setting the foreground color is not sufficient and does not work
		//result.append("<font color=\"black\"> </font>");
		return result.toString();
	}

}
