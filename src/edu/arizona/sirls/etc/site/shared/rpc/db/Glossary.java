package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;

public class Glossary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204689349599367413L;
	private int id;
	private String name;
	private long created;
	
	public Glossary() { }
	
	public Glossary(int id, String name, long created) {
		this.id = id;
		this.name = name;
		this.created = created;
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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
	
	

}
