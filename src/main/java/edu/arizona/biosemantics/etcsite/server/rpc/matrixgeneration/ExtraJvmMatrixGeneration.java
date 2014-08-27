package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;

public class ExtraJvmMatrixGeneration extends ExtraJvmCallable<Boolean> implements MatrixGeneration {

	private String inputDir;
	private String outputFile;

	public ExtraJvmMatrixGeneration(String inputDir, String outputFile) {
		this.inputDir = inputDir;
		this.outputFile = outputFile;
		
		this.setArgs(createArgs());
		//could be reduced to only libraries relevant to matrixgeneration
		if(Configuration.classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.classpath);
		this.setMainClass(edu.arizona.biosemantics.matrixgeneration.Main.class);
	}
	
	private String[] createArgs() {
		String[] args = new String[2];
		args[0] = inputDir;
		args[1] = outputFile;
		return args;
	}

	@Override
	public Boolean createReturn() {
		return exitStatus == 0;
	}
	
	public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		ExtraJvmMatrixGeneration mg = new ExtraJvmMatrixGeneration("C:/test/Test_mmm", "C:/test/Test_mmm.mx");
		mg.call();
		
	}

}
