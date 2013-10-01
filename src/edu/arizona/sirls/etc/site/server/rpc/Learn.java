package edu.arizona.sirls.etc.site.server.rpc;

import java.util.concurrent.Callable;

public class Learn implements Callable<LearnResult> {

	private int otoId;
	
	public int getOtoId() {
		return otoId;
	}

	@Override
	public LearnResult call() throws Exception {
		Thread.sleep(10000);
		otoId = 54;
		return new LearnResult(otoId);
	}

}
