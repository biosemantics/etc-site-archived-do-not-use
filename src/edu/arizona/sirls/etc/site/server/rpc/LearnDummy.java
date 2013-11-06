package edu.arizona.sirls.etc.site.server.rpc;

public class LearnDummy implements ILearn {

	private int otoUploadId;
	private String otoSecret;
	
	public int getOtoUploadId() {
		return otoUploadId;
	}

	
	
	public String getOtoSecret() {
		return otoSecret;
	}



	public void setOtoSecret(String otoSecret) {
		this.otoSecret = otoSecret;
	}



	public void setOtoUploadId(int otoUploadId) {
		this.otoUploadId = otoUploadId;
	}



	@Override
	public LearnResult call() throws Exception {
		Thread.sleep(10000);
		otoUploadId = 179;
		otoSecret = "abcdef";
		return new LearnResult(otoUploadId, otoSecret);
	}

}
