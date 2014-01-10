package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class Setup implements Serializable {
	
	private String seperator;
	private String otoLiteURL;
	private String fileBase;
	
	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public String getOtoLiteURL() {
		return otoLiteURL;
	}

	public void setOtoLiteURL(String otoLiteURL) {
		this.otoLiteURL = otoLiteURL;
	}

	public String getFileBase() {
		return fileBase;
	}

	public void setFileBase(String fileBase) {
		this.fileBase = fileBase;
	}
	
	
	
	

}
