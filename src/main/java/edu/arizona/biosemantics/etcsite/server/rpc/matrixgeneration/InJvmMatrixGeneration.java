package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.util.HashSet;
import java.util.Set;

import edu.arizona.biosemantics.etcsite.server.Task.FailHandler;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;

public class InJvmMatrixGeneration implements MatrixGeneration {
	
	private String inputDir;
	private String outputFile;
	private boolean executedSuccessfully = false;
	
	public InJvmMatrixGeneration(String inputDir, String outputFile) {
		this.inputDir = inputDir;
		this.outputFile = outputFile;
	}
	
	@Override
	public Void call() throws Exception {
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
			handleFail();
		}
		return null;
	}

	protected void handleFail() {
		for(FailHandler failHandler : failHandlers) {
			failHandler.onFail();
		}
	}

	@Override
	public void destroy() { }

	private Set<FailHandler> failHandlers = new HashSet<FailHandler>();
	
	@Override
	public void addFailHandler(FailHandler handler) {
		failHandlers.add(handler);
	}
	
	@Override
	public void removeFailHandler(FailHandler handler) {
		failHandlers.remove(handler);
	}

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
