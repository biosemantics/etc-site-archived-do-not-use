package edu.arizona.sirls.etc.site.shared.rpc.db;

public class DatasetPrefix {

	private String prefix;
	private String glossaryVersion;
	private int otoId;
	private long created;

	public DatasetPrefix(String prefix, String glossaryVersion, int otoId, long created) {
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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	
	
}
