package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;

public class Glossary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204689349599367413L;
	private int id;
	private String name;
	
	public Glossary() { }
	
	public Glossary(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
