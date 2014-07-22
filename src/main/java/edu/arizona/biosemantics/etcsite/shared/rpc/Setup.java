package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class Setup implements Serializable {
	
	private String seperator;
	private String otoLiteReviewURL;
	private String fileBase;
	private String googleRedirectURI;
	private String googleClientId;
	private String deploymentUrl;
	
	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public String getOtoLiteReviewURL() {
		return otoLiteReviewURL;
	}

	public void setOtoLiteReviewURL(String otoLiteReviewURL) {
		this.otoLiteReviewURL = otoLiteReviewURL;
	}

	public String getFileBase() {
		return fileBase;
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

	public String getDeploymentUrl() {
		return deploymentUrl;
	}
	
	public void setDeploymentUrl(String deploymentUrl) {
		this.deploymentUrl = deploymentUrl;
	}
	
}
