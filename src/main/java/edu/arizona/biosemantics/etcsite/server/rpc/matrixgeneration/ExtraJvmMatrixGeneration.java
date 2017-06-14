package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;

public class ExtraJvmMatrixGeneration extends ExtraJvmCallable<Void> implements MatrixGeneration {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			try {
				edu.arizona.biosemantics.matrixgeneration.CLIMain.main(args);
			} catch (Throwable t) {
				System.out.println("ExtraJvmMatrixGeneration failed with throwable "+t.getMessage());
				System.out.println(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
				
				if(t.getCause()!=null) System.out.println("caused by "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t.getCause()));
				System.exit(-1);
			}
		}
		
	}
	
	private String inputDir;
	private String outputFile;
	private boolean inheritValues;
	private boolean generateAbsentPresent;
	private boolean inferCharactersFromOntologies;
	private String taxonGroup;

	public ExtraJvmMatrixGeneration(String inputDir, String taxonGroup, String outputFile, boolean inheritValues, 
			boolean generateAbsentPresent, boolean inferCharactersFromOntologies) {
		this.inputDir = inputDir;
		this.taxonGroup = taxonGroup;
		this.outputFile = outputFile;
		this.inheritValues = inheritValues;
		this.generateAbsentPresent = generateAbsentPresent;
		this.inferCharactersFromOntologies = inferCharactersFromOntologies;
		
		this.setArgs(createArgs());
		//could be reduced to only libraries relevant to matrixgeneration
		if(!Configuration.matrixgeneration_xms.isEmpty()) 
			this.setXms(Configuration.matrixgeneration_xms);
		if(!Configuration.matrixgeneration_xmx.isEmpty()) 
			this.setXmx(Configuration.matrixgeneration_xmx);
		if(Configuration.matrixgeneration_classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.matrixgeneration_classpath);
		this.setMainClass(MainWrapper.class);
	}
	
	private String[] createArgs() {
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
		return args;
	}

	private void addArg(List<String> argList, String arg, String value) {
		argList.add("-" + arg);
		argList.add(value);
	}

	private void addArg(List<String> argList, String arg) {
		argList.add("-" + arg);
	}
	
	@Override
	public Void createReturn() throws MatrixGenerationException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Matrix generation failed.");
			throw new MatrixGenerationException();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		ExtraJvmMatrixGeneration mg = new ExtraJvmMatrixGeneration("C:/etcsitebase/etcsite/data/users/4/test_mg", "BACTERIA", "C:/micropie/outputser/ext.ser", true, 
				true, true);
		mg.call();
		
	}


}