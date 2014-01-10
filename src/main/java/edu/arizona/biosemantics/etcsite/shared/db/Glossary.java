package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

public class Glossary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204689349599367413L;
	private int id;
	private String name;
	private Date created;
	
	public Glossary() { }
	
	public Glossary(int id, String name, Date created) {
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		Glossary glossary = (Glossary)object;
		if(glossary.getId()==this.id)
			return true;
		return false;
	}

}
