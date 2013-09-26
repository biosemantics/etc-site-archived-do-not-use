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
	
	public User() { }
	
	public User(int id, String name, String password, long created) {
		this.id = id;
		this.name = name;
		this.password = password;
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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
	
	
	
}
