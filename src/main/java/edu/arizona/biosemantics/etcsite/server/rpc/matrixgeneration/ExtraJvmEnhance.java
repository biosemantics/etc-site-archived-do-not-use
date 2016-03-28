package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.ExtraJvmCallable;
import edu.arizona.biosemantics.etcsite.server.enhance.EnhanceRun;
import edu.arizona.biosemantics.etcsite.server.enhance.MinimalEnhanceRun;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;

public class ExtraJvmEnhance extends ExtraJvmCallable<Void> implements Enhance {

	public static class MainWrapper {
		
		public static void main(String[] args) {
			String inputDir = args[0];
			String tempDir = args[1];
			String inputOntology = args[2];
			String termReviewTermCategorization = args[3];
			String termReviewSynonyms = args[4];
			String taxonGroup = args[5];
			
			try {
				if(inputOntology != null && termReviewTermCategorization != null && termReviewSynonyms != null) {
					System.out.println("Run Enhance: \n" + inputDir + " \n" + tempDir + " \n" + inputOntology + "\n " + termReviewTermCategorization + "\n"
							+ termReviewSynonyms + "\n" + taxonGroup );
					EnhanceRun enhance = new EnhanceRun(inputDir, tempDir, inputOntology, 
							termReviewTermCategorization, termReviewSynonyms, TaxonGroup.valueOf(taxonGroup));
					enhance.run();
					System.out.println("Done Running Minimal Enhance");
				} else {
					System.out.println("Run Minimal Enhance: \n" + inputDir + " \n" + tempDir + " \n" + taxonGroup );
					MinimalEnhanceRun enhance = new MinimalEnhanceRun(inputDir, tempDir, TaxonGroup.valueOf(taxonGroup));
					enhance.run();
					System.out.println("Done Running Minimal Enhance");
				}
					
			} catch (Throwable t) {
				System.out.println("ExtraJvmEnhance failed with throwable "+t.getMessage());
				System.out.println(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t));
				
				if(t.getCause()!=null) System.out.println("caused by "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t.getCause()));
				System.exit(-1);
			}
		}
	}
	
	private String inputDir;
	private String outputDir;
	private String inputOntology;
	private String termReviewTermCategorization;
	private String termReviewSynonyms;
	private String taxonGroup;

	public ExtraJvmEnhance(String inputDir, String outputDir, 
			String inputOntology, String termReviewTermCategorization, String termReviewSynonyms, 
			String taxonGroup) {
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.inputOntology = inputOntology;
		this.termReviewTermCategorization = termReviewTermCategorization;
		this.termReviewSynonyms = termReviewSynonyms;
		this.taxonGroup = taxonGroup;
		
		this.setArgs(createArgs());
		//could be reduced to only libraries relevant to matrixgeneration
		if(!Configuration.matrixgeneration_xms.isEmpty()) 
			this.setXms(Configuration.matrixgeneration_xms);
		if(!Configuration.matrixgeneration_xmx.isEmpty()) 
			this.setXmx(Configuration.matrixgeneration_xmx);
		if(Configuration.classpath.isEmpty())
			this.setClassPath(System.getProperty("java.class.path"));
		else
			this.setClassPath(Configuration.classpath);
		this.setMainClass(MainWrapper.class);
	}
	
	private String[] createArgs() {
		String[] args = new String[] { inputDir, outputDir, inputOntology, 
				termReviewTermCategorization, termReviewSynonyms, taxonGroup};
		return args;
	}
	
	@Override
	public Void createReturn() throws MatrixGenerationException {
		if(exitStatus != 0) {
			log(LogLevel.ERROR, "Enhance failed.");
			throw new MatrixGenerationException();
		}
		return null;
	}
	
	/*public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		ExtraJvmMatrixGeneration mg = new ExtraJvmMatrixGeneration("C:/test/Test_mmm", "", "PLANT", "C:/test/Test_mmm.mx", true, 
				true, true);
		mg.call();
		
	}*/


}
