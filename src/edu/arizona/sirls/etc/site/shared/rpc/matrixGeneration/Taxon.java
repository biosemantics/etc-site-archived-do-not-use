package edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Taxon implements Serializable {

	private String name;
	private Map<String, String> characters = new HashMap<String, String>();
	private String id;
	
	public Taxon() { }	
	
	public Taxon(String id, String name, Map<String, String> characters) {
		this.id = id;
		this.name = name;
		this.characters = characters;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCharacterState(String characterName) {
		if(!characters.containsKey(characterName) || characters.get(characterName)==null)
			return "";
		return characters.get(characterName).trim();
	}
	
	public void setCharacterState(String characterName, String characterState) {
		characters.put(characterName, characterState);
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

}
