package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

public class Configuration implements Serializable {
	
	private static final long serialVersionUID = -3601068865826034113L;
	private int id;
	private Date created;
	
	public Configuration() { }
	
	public Configuration(int id, Date created) {
		super();
		this.id = id;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
		Configuration configuration = (Configuration)object;
		if(configuration.getId()==this.id)
			return true;
		return false;
	}
	
}
