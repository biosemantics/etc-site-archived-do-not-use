package edu.arizona.sirls.etc.site.shared.rpc.file;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BracketValidator {

	private Map<Character, Character> brackets = new LinkedHashMap<Character, Character>();
	
	public BracketValidator() {
		brackets.put('(', ')');
		brackets.put(')', '(');
		brackets.put('[', ']');
		brackets.put(']', '[');
		brackets.put('{', '}');
		brackets.put('}', '{');
	}
	
	public Map<Character, Integer> getBracketCountDifferences(String text) {
		LinkedHashMap<Character, Integer> result = new LinkedHashMap<Character, Integer>();
		LinkedHashMap<Character, Integer> bracketCounts = new LinkedHashMap<Character, Integer>();
		for(Character character : brackets.keySet())
			bracketCounts.put(character, 0);
		
		for(int i=0; i<text.length(); i++) {
			char currentCharacter = text.charAt(i);
			if(brackets.containsKey(currentCharacter)) {
				bracketCounts.put(currentCharacter, bracketCounts.get(currentCharacter)+1);
			}
		}
		
		for(Character character : bracketCounts.keySet()) {
			int count = bracketCounts.get(character);
			int oppositeCount = bracketCounts.get(brackets.get(character));
			result.put(character, count - oppositeCount);
		}
		return result;
	}
	
	public boolean validate(String text) {
		Map<Character, Integer> countDifferences = this.getBracketCountDifferences(text);
		for(Integer count : countDifferences.values())
			if(count != 0)
				return false;
		return true;
	}
	
}
