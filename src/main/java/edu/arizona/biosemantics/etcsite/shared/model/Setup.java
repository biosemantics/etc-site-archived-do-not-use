package edu.arizona.biosemantics.etcsite.shared.model;

import java.io.Serializable;

public class Setup implements Serializable {
	
	private String seperator;
	private String fileBase;
	private String publicFolder;
	private String googleRedirectURI;
	private String googleClientId;
	
	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public String getFileBase() {
		return fileBase;
	}
	
	public String getPublicFolder() {
		return publicFolder;
	}

	public void setPublicFolder(String publicFolder) {
		this.publicFolder = publicFolder;
	}

	public void setFileBase(String fileBase) {
		this.fileBase = fileBase;
	}

	public String getGoogleRedirectURI() {
		return googleRedirectURI;
	}

	public void setGoogleRedirectURI(String googleRedirectURI) {
		this.googleRedirectURI = googleRedirectURI;
	}

	public String getGoogleClientId() {
		return googleClientId;
	}

	public void setGoogleClientId(String googleClientId) {
		this.googleClientId = googleClientId;
	}
}
