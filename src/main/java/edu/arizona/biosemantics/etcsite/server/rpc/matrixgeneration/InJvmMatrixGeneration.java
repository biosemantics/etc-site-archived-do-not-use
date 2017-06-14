package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.matrixgeneration.CLIMain;

public class InJvmMatrixGeneration implements MatrixGeneration {
	
	private String inputDir;
	private String taxonGroup;
	private String outputFile;	
	private boolean inheritValues;
	private boolean generateAbsentPresent;
	private boolean inferCharactersFromOntologies;
	
	private boolean executedSuccessfully = false;
	
	public InJvmMatrixGeneration(String inputDir, String taxonGroup, String outputFile, boolean inheritValues, 
			boolean generateAbsentPresent, boolean inferCharactersFromOntologies) {
		this.inputDir = inputDir;
		this.taxonGroup = taxonGroup;
		this.outputFile = outputFile;
		this.inheritValues = inheritValues;
		this.generateAbsentPresent = generateAbsentPresent;
		this.inferCharactersFromOntologies = inferCharactersFromOntologies;
	}
	
	@Override
	public Void call() throws MatrixGenerationException {
		List<String> argList = new LinkedList<String>();
		addArg(argList, "input", inputDir);
		addArg(argList, "output", outputFile);
		if(inheritValues) {
			//addArg(argList, "up_taxonomy_inheritance");
			addArg(argList, "down_taxonomy_inheritance");
		}
		if(generateAbsentPresent) {
			addArg(argList, "presence_relation");
			addArg(argList, "presence_entity");
		}
		if(inferCharactersFromOntologies) {
			addArg(argList, "up_ontology_inheritance");
			addArg(argList, "down_ontology_inheritance");
		}
		addArg(argList, "taxon_group", taxonGroup);
		addArg(argList, "output_format", "serialize");
		
		String[] args = argList.toArray(new String[argList.size()]);
		System.out.println(args);
		try {
			CLIMain.main(args);
			executedSuccessfully = true;
		} catch(Throwable e) {
			log(LogLevel.ERROR, "Matrix generation failed with exception.", e);
			executedSuccessfully = false;
		}
		if(!isExecutedSuccessfully()) {
			throw new MatrixGenerationException();
		}
		return null;
	}

	private void addArg(List<String> argList, String arg, String value) {
		argList.add("-" + arg);
		argList.add(value);
	}

	private void addArg(List<String> argList, String arg) {
		argList.add("-" + arg);
	}

	@Override
	public void destroy() { }

	@Override
	public boolean isExecutedSuccessfully() {
		return executedSuccessfully;
	}

	public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		InJvmMatrixGeneration mg = new InJvmMatrixGeneration("C:\\etcsitebase\\etcsite\\data\\users\\4\\test_mg", "BACTERIA", "C:/micropie/outputser/Matrix.mx", true, true, true);
		mg.call();
		
	}
	
}