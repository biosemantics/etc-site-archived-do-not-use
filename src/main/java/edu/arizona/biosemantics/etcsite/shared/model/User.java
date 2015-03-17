package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

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

	private String otoAuthenticationToken;
	private String otoAccountEmail;
	
	private boolean matrixGenerationEmailChk;
	private boolean treeGenerationEmailChk;
	private boolean textCaptureEmailChk;
	private boolean taxonomyComparisonEmailChk;
	
	public User() { }
	
	public User(int id, String openIdProviderId, String openIdProvider, String password, String firstName, String lastName, String email, String affiliation, String bioportalUserId, 
			String bioportalAPIKey, String otoAccountEmail, String otoAuthenticationToken, Date created) {
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
	
	public User(String firstName, String lastName, String email, boolean matrixGenerationEmailChk,
	boolean treeGenerationEmailChk,boolean textCaptureEmailChk,boolean taxonomyComparisonEmailChk)
	 {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.matrixGenerationEmailChk = matrixGenerationEmailChk;
		this.treeGenerationEmailChk = treeGenerationEmailChk;
		this.textCaptureEmailChk = textCaptureEmailChk;
		this.taxonomyComparisonEmailChk = taxonomyComparisonEmailChk;
		this.otoAccountEmail = otoAccountEmail;
		this.otoAuthenticationToken = otoAuthenticationToken;
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
	
	public boolean getMatrixGenerationEmailChk() {
		return matrixGenerationEmailChk;
	}

	public void setMatrixGenerationEmailChk(boolean matrixGenerationEmailChk) {
		this.matrixGenerationEmailChk = matrixGenerationEmailChk;
	}
	
	public boolean getTreeGenerationEmailChk() {
		return treeGenerationEmailChk;
	}

	public void setTreeGenerationEmailChk(boolean treeGenerationEmailChk) {
		this.treeGenerationEmailChk = treeGenerationEmailChk;
	}

	public boolean getTextCaptureEmailChk() {
		return textCaptureEmailChk;
	}

	public void setTextCaptureEmailChk(boolean textCaptureEmailChk) {
		this.textCaptureEmailChk = textCaptureEmailChk;
	}

	public boolean getTaxonomyComparisonEmailChk() {
		return taxonomyComparisonEmailChk;
	}

	public void setTaxonomyComparisonEmailChk(boolean taxonomyComparisonEmailChk) {
		this.taxonomyComparisonEmailChk = taxonomyComparisonEmailChk;
	}

	
}
