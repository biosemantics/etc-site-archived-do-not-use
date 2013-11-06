package edu.arizona.sirls.etc.site.server.rpc;

public class LearnResult {


	private int otoUploadId;
	private String otoSecret;

	public LearnResult(int otoUploadId, String otoSecret) {
		this.otoUploadId = otoUploadId;
		this.otoSecret = otoSecret;
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


	
}
