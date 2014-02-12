package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class Setup implements Serializable {
	
	private String seperator;
	private String otoLiteReviewURL;
	private String fileBase;
	
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
	
	
	
	

}
