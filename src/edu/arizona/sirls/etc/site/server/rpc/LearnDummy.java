package edu.arizona.sirls.etc.site.server.rpc;

public class LearnDummy implements ILearn {

	private int otoId;
	
	public int getOtoId() {
		return otoId;
	}

	@Override
	public LearnResult call() throws Exception {
		Thread.sleep(10000);
		otoId = 179;
		return new LearnResult(otoId);
	}

}
