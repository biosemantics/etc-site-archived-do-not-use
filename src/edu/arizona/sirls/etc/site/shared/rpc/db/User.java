package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3593412522402966872L;
	private int id;
	private String name;
	private String password;
	private long created;
	private String bioportalUserId;
	private String bioportalAPIKey;
	
	public User() { }
	
	public User(int id, String name, String password, String bioportalUserId, String bioportalAPIKey, long created) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.created = created;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
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
	
	
	
}
