package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = 3596572148232966822L;		//old: 3593412522402966872L;
	private int uniqueId; //the totally unique integer id identifying this User. 
	
	private String nonUniqueId; //the realm-unique value provided by the openID provider, or the user's email address if a local account. 
	private String openIdProvider; //e.g. "Google", "Yahoo".  "none" if local account. 
	private String password; //only used if local account. Otherwise, openID provider handles authentication. 
	
	private String firstName;
	private String lastName;
	private String email;
	private String affiliation;
	private String bioportalUserId;
	private String bioportalAPIKey;
	private Date created;
	
	public User() { }
	
	public User(int uniqueId, String nonUniqueId, String openIdProvider, String password, String firstName, String lastName, String email, String affiliation, String bioportalUserId, String bioportalAPIKey, Date created) {
		this.uniqueId = uniqueId;
		this.nonUniqueId = nonUniqueId;
		this.openIdProvider = openIdProvider;
		this.password = password;
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.affiliation = affiliation;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
		this.created = created;
	}
	
	@Override
	public int hashCode() {
		return uniqueId;
	}
	
	public int getUniqueId(){
		return uniqueId;
	}
	
	public String getNonUniqueId() {
		return nonUniqueId;
	}

	public void setNonUniqueId(String uniqueId) {
		this.nonUniqueId = uniqueId;
	}

	public String getOpenIdProvider() {
		return openIdProvider;
	}

	public void setOpenIdProvider(String openIdProvider) {
		this.openIdProvider = openIdProvider;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
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
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		User user = (User)object;
		if(user.getUniqueId() == (this.uniqueId))
			return true;
		return false;
	}
	
	public String toString(){
		return "\nUser:"
				+ "\n\tUnique ID: " + uniqueId
				+ "\n\tNon-unique ID: " + nonUniqueId
				+ "\n\tOpenID Provider: " + openIdProvider
				+ "\n\tPassword: " + password
				+ "\n\tFirst Name: " + firstName
				+ "\n\tLast Name: " + lastName
				+ "\n\tEmail: " + email
				+ "\n\tAffiliation: " + affiliation
				+ "\n\tBioportal API Key: " + bioportalAPIKey
				+ "\n\tBioportal User ID: " + bioportalUserId
				+ "\n\tCreated: " + created;
	}
}
