package edu.arizona.sirls.etc.site.server.rpc;

import java.util.concurrent.Callable;

public class MatrixGeneration implements Callable<Boolean> {

	private String inputDir;
	private String outputFile;

	public MatrixGeneration(String inputDir, String outputFile) {
		this.inputDir = inputDir;
		this.outputFile = outputFile;
	}
	
	@Override
	public Boolean call() throws Exception {
		String[] args = { inputDir, outputFile };
		try {
			matrixgeneration.MatrixGeneration.main(args); 
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
