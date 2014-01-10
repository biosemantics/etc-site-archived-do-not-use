package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = 3593412522402966872L;
	private int id;
	private String name;
	private String password;
	private String bioportalUserId;
	private String bioportalAPIKey;
	private Date created;
	
	public User() { }
	
	public User(int id, String name, String password, String bioportalUserId, String bioportalAPIKey, Date created) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBioportalUserId() {
		return bioportalUserId;
	}

	public void setBioportalUserId(String bioportalUserId) {
		this.bioportalUserId = bioportalUserId;
	}

	public String getBioportalAPIKey() {
		return bioportalAPIKey;
	}

	public void setBioportalAPIKey(String bioportalAPIKey) {
		this.bioportalAPIKey = bioportalAPIKey;
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
		User user = (User)object;
		if(user.getId()==this.id)
			return true;
		return false;
	}
}
