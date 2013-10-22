package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.Serializable;
import java.util.Date;

public class DatasetPrefix implements Serializable {

	private static final long serialVersionUID = -446256658122228182L;
	private String prefix;
	private String glossaryVersion;
	private int otoId;
	private Date created;

	public DatasetPrefix(String prefix, String glossaryVersion, int otoId, Date created) {
		this.prefix = prefix;
		this.glossaryVersion = glossaryVersion;
		this.otoId = otoId;
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

	public int getOtoId() {
		return otoId;
	}

	public void setOtoId(int otoId) {
		this.otoId = otoId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}



	
	
}
