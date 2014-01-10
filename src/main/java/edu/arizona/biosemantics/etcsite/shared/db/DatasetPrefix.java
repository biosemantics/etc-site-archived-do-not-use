package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.Serializable;
import java.util.Date;

public class DatasetPrefix implements Serializable {

	private static final long serialVersionUID = -446256658122228182L;
	private String prefix;
	private String glossaryVersion;
	private int otoUploadId;
	private String otoSecret;
	private Date created;

	public DatasetPrefix() { }
	
	public DatasetPrefix(String prefix, String glossaryVersion, int otoUploadId, String otoSecret, Date created) {
		this.prefix = prefix;
		this.glossaryVersion = glossaryVersion;
		this.otoUploadId = otoUploadId;
		this.otoSecret = otoSecret;
		this.created = created;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getGlossaryVersion() {
		return glossaryVersion;
	}

	public void setGlossaryVersion(String glossaryVersion) {
		this.glossaryVersion = glossaryVersion;
	}

	public int getOtoUploadId() {
		return otoUploadId;
	}

	public void setOtoUploadId(int otoUploadId) {
		this.otoUploadId = otoUploadId;
	}

	public String getOtoSecret() {
		return otoSecret;
	}

	public void setOtoSecret(String otoSecret) {
		this.otoSecret = otoSecret;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public int hashCode() {
		return prefix.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null)
			return false;
		if (getClass() != object.getClass()) {
	        return false;
	    }
		DatasetPrefix dataSetPrefix = (DatasetPrefix)object;
		if(dataSetPrefix.getPrefix()==this.getPrefix())
			return true;
		return false;
	}

	
	
}
