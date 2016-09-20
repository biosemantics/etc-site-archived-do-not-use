package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

	public static enum EmailPreference {
		TEXT_CAPTURE, MATRIX_GENERATION, TREE_GENERATION, TAXONOMY_COMPARISON, VISUALIZATION, PIPELINE;

		public String getKey() {
			return this.getClass() + "_" + this.name();
		}
	}
	
	private int id;
	
	private String openIdProviderId = ""; //the realm-unique value provided by the openID provider, or the user's email address if a local account. 
	private String openIdProvider = ""; //e.g. "Google", "Yahoo".  "none" if local account. 
	private String password = ""; //only used if local account. Otherwise, openID provider handles authentication. 
	
	private String firstName = "";
	private String lastName = "";
	private String email = "";
	private String affiliation = "";
	private String bioportalUserId = "";
	private String bioportalAPIKey = "";
	private Date created;

	private String otoAuthenticationToken = "";
	private String otoAccountEmail = "";
	
	private Map<String , Boolean> profile = new HashMap<String, Boolean>();
		
	public User() { }
	
	public User(int id, String openIdProviderId, String openIdProvider, String password, String firstName, String lastName, String email, String affiliation, String bioportalUserId, 
			String bioportalAPIKey, String otoAccountEmail, String otoAuthenticationToken, Map<String, Boolean> profile, Date created) {
		this.id = id;
		this.openIdProviderId = openIdProviderId;
		this.openIdProvider = openIdProvider;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.affiliation = affiliation;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
		this.otoAccountEmail = otoAccountEmail;
		this.otoAuthenticationToken = otoAuthenticationToken;
		this.profile = profile;
		this.created = created;
	}
	
	public User(String openIdProviderId, String openIdProvider, String password, String firstName, String lastName, 
			String affiliation, String bioportalUserId, String bioportalAPIKey, String otoAccountEmail, String otoAuthenticationToken) {
		this.openIdProviderId = openIdProviderId;
		this.openIdProvider = openIdProvider;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = openIdProviderId;
		this.affiliation = affiliation;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
		this.otoAccountEmail = otoAccountEmail;
		this.otoAuthenticationToken = otoAuthenticationToken;
	}
	
	public User(String password, String firstName, String lastName, 
			String email, String affiliation, String bioportalUserId, String bioportalAPIKey, String otoAccountEmail, String otoAuthenticationToken) {
		this.openIdProvider = "none";
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.affiliation = affiliation;
		this.bioportalUserId = bioportalUserId;
		this.bioportalAPIKey = bioportalAPIKey;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public int getId(){
		return id;
	}
	
	public String getOpenIdProviderId() {
		return openIdProviderId;
	}

	public void setOpenIdProviderId(String openIdProviderId) {
		this.openIdProviderId = openIdProviderId;
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
	
	
	public void setOtoAccountEmail(String otoAccountEmail) {
		this.otoAccountEmail = otoAccountEmail;
	}
	
	public String getOtoAuthenticationToken() {
		return otoAuthenticationToken;
	}

	public void setOtoAuthenticationToken(String otoAuthenticationToken) {
		this.otoAuthenticationToken = otoAuthenticationToken;
	}

	public String getOtoAccountEmail() {
		return otoAccountEmail;
	}

	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		User user = (User)object;
		if(user.getId() == (this.id))
			return true;
		return false;
	}
	
	public String toString(){
		return "\nUser:"
				+ "\n\tID: " + id
				+ "\n\tOpenID Provider ID: " + openIdProviderId
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

	public boolean getProfileValue(String key) {
		boolean profileValue = false;
		if (profile.get(key) != null) {
			profileValue = profile.get(key);
		}
		return profileValue;
	}

	public void setProfileValue(String key, boolean value) {
		this.profile.put(key, value);
	}
	
	public Map<String, Boolean> getProfile() {
		return profile;
	}
	
	public void setProfile(Map<String, Boolean> profile) {
		this.profile = profile;
	}
	
	public boolean hasOTOAuthenticationTokenAndEmail() {
		return this.otoAccountEmail != null && this.otoAuthenticationToken != null && 
				!this.otoAccountEmail.isEmpty() && !this.otoAuthenticationToken.isEmpty();
	}
	
}
