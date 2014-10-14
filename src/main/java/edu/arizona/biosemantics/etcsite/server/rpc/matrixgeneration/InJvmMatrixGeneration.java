package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;

public class InJvmMatrixGeneration implements MatrixGeneration {
	
	private String inputDir;
	private String outputFile;
	private boolean executedSuccessfully = false;
	
	public InJvmMatrixGeneration(String inputDir, String outputFile) {
		this.inputDir = inputDir;
		this.outputFile = outputFile;
	}
	
	@Override
	public Void call() throws MatrixGenerationException {
		//String[] args = { inputDir, outputFile };
		try {
			edu.arizona.biosemantics.matrixgeneration.Main main = new edu.arizona.biosemantics.matrixgeneration.Main(inputDir, outputFile);
			main.run();
			//edu.arizona.biosemantics.matrixgeneration.MatrixGeneration.main(args); 
			executedSuccessfully = true;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Matrix generation failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new MatrixGenerationException(null);
		}
		return null;
	}

	@Override
	public void destroy() { }

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

	public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		InJvmMatrixGeneration mg = new InJvmMatrixGeneration("C:/test/Test_mmm", "C:/test/Test_mmm.mx");
		mg.call();
		
	}
	
}
