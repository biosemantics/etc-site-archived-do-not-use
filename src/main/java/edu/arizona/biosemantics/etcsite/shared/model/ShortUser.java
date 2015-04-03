package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;

public class ShortUser implements Serializable {
	
	private static final long serialVersionUID = 9014388467462637993L;
	private int id;
	private String email = "";
	private String firstName = "";
	private String lastName = "";
	private String affiliation = "";
	private String openIdProvider = "";
	private String openIdProviderId = "";
	private String bioportalUserId = "";
	private String bioportalApiKey = "";
	private String otoAccountEmail = "";
	
	private boolean matrixGenerationEmail;
	private boolean treeGenerationEmail;
	private boolean textCaptureEmail;
	private boolean taxonomyComparisonEmail;
	
	public ShortUser() { }
	
	public ShortUser(int id, String email, String firstName, String lastName, String affiliation, 
			String openIdProvider, String openIdProivderId, String bioportalUserId, 
			String bioportalApiKey, String otoAccountEmail, 
			boolean textCaptureEmail, boolean matrixGenerationEmail, boolean treeGenerationEmail,
			boolean taxonomyComparisonEmail) {
		this.id = id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.affiliation = affiliation;
		this.openIdProvider = openIdProvider;
		this.openIdProviderId = openIdProivderId;
		this.bioportalUserId = bioportalUserId;
		this.bioportalApiKey = bioportalApiKey;
		this.otoAccountEmail = otoAccountEmail;
		
		this.textCaptureEmail = textCaptureEmail;
		this.matrixGenerationEmail = matrixGenerationEmail;
		this.treeGenerationEmail = treeGenerationEmail;
		this.taxonomyComparisonEmail = taxonomyComparisonEmail;
	}

	public ShortUser(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.affiliation = user.getAffiliation();
		this.openIdProvider = user.getOpenIdProvider();
		this.openIdProviderId = user.getOpenIdProviderId();
		this.bioportalUserId = user.getBioportalUserId();
		this.bioportalApiKey = user.getBioportalAPIKey();
		this.matrixGenerationEmail = user.isMatrixGenerationEmail();
		this.treeGenerationEmail = user.isTreeGenerationEmail();
		this.textCaptureEmail = user.isTextCaptureEmail();
		this.taxonomyComparisonEmail = user.isTaxonomyComparisonEmail();
		this.otoAccountEmail = user.getOtoAccountEmail();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
	public String getOpenIdProvider() {
		return openIdProvider;
	}

	public void setOpenIdProvider(String openIdProvider) {
		this.openIdProvider = openIdProvider;
	}

	public String getOpenIdProviderId() {
		return openIdProviderId;
	}

	public void setOpenIdProviderId(String openIdProviderId) {
		this.openIdProviderId = openIdProviderId;
	}

	public String getBioportalUserId() {
		return bioportalUserId;
	}

	public void setBioportalUserId(String bioportalUserId) {
		this.bioportalUserId = bioportalUserId;
	}

	public String getBioportalApiKey() {
		return bioportalApiKey;
	}

	public void setBioportalApiKey(String bioportalApiKey) {
		this.bioportalApiKey = bioportalApiKey;
	}

	public String getOtoAccountEmail() {
		return otoAccountEmail;
	}

	public void setOtoAccountEmail(String otoAccountEmail) {
		this.otoAccountEmail = otoAccountEmail;
	}

	public String getFullName() {
		return firstName + " "  + lastName;
	}
	
	public String getFullNameEmail() {
		return getFullName() + " (" + email + ") ";
	}
	
	public String getFullNameEmailAffiliation() {
		if(affiliation.isEmpty()) 
			return getFullNameEmail();
		return getFullNameEmail() + " at " + affiliation;
	}
	

	public boolean isMatrixGenerationEmail() {
		return matrixGenerationEmail;
	}

	public void setMatrixGenerationEmail(boolean matrixGenerationEmail) {
		this.matrixGenerationEmail = matrixGenerationEmail;
	}

	public boolean isTreeGenerationEmail() {
		return treeGenerationEmail;
	}

	public void setTreeGenerationEmail(boolean treeGenerationEmail) {
		this.treeGenerationEmail = treeGenerationEmail;
	}

	public boolean isTextCaptureEmail() {
		return textCaptureEmail;
	}

	public void setTextCaptureEmail(boolean textCaptureEmail) {
		this.textCaptureEmail = textCaptureEmail;
	}

	public boolean isTaxonomyComparisonEmail() {
		return taxonomyComparisonEmail;
	}

	public void setTaxonomyComparisonEmail(boolean taxonomyComparisonEmail) {
		this.taxonomyComparisonEmail = taxonomyComparisonEmail;
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
		ShortUser user = (ShortUser)object;
		if(user.getId()==this.id)
			return true;
		return false;
	}


}

