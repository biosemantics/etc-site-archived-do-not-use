package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

public class InJvmMatrixGeneration implements MatrixGeneration {
	
	private String inputDir;
	private String outputFile;

	public InJvmMatrixGeneration(String inputDir, String outputFile) {
		this.inputDir = inputDir;
		this.outputFile = outputFile;
	}
	
	@Override
	public Boolean call() throws Exception {
		//String[] args = { inputDir, outputFile };
		try {
			edu.arizona.biosemantics.matrixgeneration.Main main = new edu.arizona.biosemantics.matrixgeneration.Main(inputDir, outputFile);
			main.run();
			//edu.arizona.biosemantics.matrixgeneration.MatrixGeneration.main(args); 
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void destroy() { }
	
	public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		InJvmMatrixGeneration mg = new InJvmMatrixGeneration("C:/test/Test_mmm", "C:/test/Test_mmm.mx");
		mg.call();
		
	}

}
