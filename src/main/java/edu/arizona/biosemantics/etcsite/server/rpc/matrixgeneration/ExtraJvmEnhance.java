package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
			//System.out.println(args);
			String inputDir = null;
			String tempDir = null;
			String inputOntology = null;
			String termReviewTermCategorization = null;
			String termReviewSynonyms = null;
			String taxonGroup = null;
			if(args.length == 6) {
				//System.out.println(6);
				inputDir = args[0];
				tempDir = args[1];
				inputOntology = args[2];
				termReviewTermCategorization = args[3];
				termReviewSynonyms = args[4];
				taxonGroup = args[5];
			} else if(args.length == 3) {
				//System.out.println(3);
				inputDir = args[0];
				tempDir = args[1];
				taxonGroup = args[2];
			}
			try {
				if(inputOntology != null && !inputOntology.isEmpty() 
						&& termReviewTermCategorization != null && !termReviewTermCategorization.isEmpty()
						&& termReviewSynonyms != null && !termReviewSynonyms.isEmpty()) {
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
	/*
	public static void main(String[] args) throws Exception {
		String inputDir = "enhance2/in";
		//"C:/Users/rodenhausen.CATNET/Downloads/cathy mg task/Weakley_Plant_1130_output_by_TC_task_Weakley_Plant_1130_12-10-2016_2";
		String tempDir = "enhance2/enhance";
		String inputOntology = "enhance2/ontology/27.owl";//"C:/Users/rodenhausen.CATNET/Downloads/cathy mg task/0_output_by_TC_task_1_output_by_OB_task_steven_rubus_ontology_reduced_12-10-2016_3/27.owl";
		String termReviewTermCategorization = "enhance2/review/category_mainterm_synonymterm-task-GC_rubus_Plant.csv";//"C:/Users/rodenhausen.CATNET/Downloads/cathy mg task/Weakley_Plant_1130_TermsReviewed_by_TC_task_Weakley_Plant_1130_12-10-2016_2/category_term-task-Weakley_Plant_1130.csv";
		String termReviewSynonyms = "enhance2/review/category_mainterm_synonymterm-task-GC_rubus_Plant.csv";//"C:/Users/rodenhausen.CATNET/Downloads/cathy mg task/Weakley_Plant_1130_TermsReviewed_by_TC_task_Weakley_Plant_1130_12-10-2016_2/category_mainterm_synonymterm-task-Weakley_Plant_1130.csv";
		String taxonGroup = "PLANT";
		
		EnhanceRun enhance = new EnhanceRun(inputDir, tempDir, inputOntology, 
				termReviewTermCategorization, termReviewSynonyms, TaxonGroup.valueOf(taxonGroup));
		enhance.run();
	}*/
	
	private String inputDir;
	private String outputDir;
	private String inputOntology;
	private String termReviewTermCategorization;
	private String termReviewSynonyms;
	private String taxonGroup;
	
	/*public static void main(String[] args) {
		MainWrapper.main(new String[] {"C:/Users/rodenhausen.CATNET/Desktop/etcsite/data/users/1/0", 
				"C:/Users/rodenhausen.CATNET/Desktop/etcsite/data/matrixGeneration/208/enhance", 
				TaxonGroup.PLANT.toString()});
	}*/
	
	/*public static void main(String[] args) {
		MainWrapper.main(new String[] {"C:/Users/rodenhausen.CATNET/Desktop/etcsite/data/users/1/0", 
				"C:/Users/rodenhausen.CATNET/Desktop/etcsite/data/matrixGeneration/209/enhance", 
				null, null, null,
				TaxonGroup.PLANT.toString()});
	}*/
	
	/*public static void main(String[] args) throws Exception {
		ExtraJvmEnhance enhance = new ExtraJvmEnhance(
				"C:/Users/rodenhausen.CATNET/Desktop/etcsite/data/users/1/0_output_by_TC_task_2", 
				"C:/Users/rodenhausen.CATNET/Desktop/etcsite/data/matrixGeneration/68/enhance", 
				"", "", "", TaxonGroup.PLANT.toString());
		enhance.call();
	}*/

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
	
	/**/
	public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		ExtraJvmEnhance mg = new ExtraJvmEnhance("C:/etcsitebase/etcsite/data/users/4/smicropie_demo_output_by_TC_task_micropiedemo", "C:/test/Test_mmm.mx", "",
				"", "", "BACTERIA");
		mg.call();
		
	}


}
