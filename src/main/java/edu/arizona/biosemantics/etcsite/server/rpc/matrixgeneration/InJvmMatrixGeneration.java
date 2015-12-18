package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import au.com.bytecode.opencsv.CSVReader;
import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.ling.know.ICharacterKnowledgeBase;
import edu.arizona.biosemantics.common.ling.know.IGlossary;
import edu.arizona.biosemantics.common.ling.know.SingularPluralProvider;
import edu.arizona.biosemantics.common.ling.know.Term;
import edu.arizona.biosemantics.common.ling.know.lib.GlossaryBasedCharacterKnowledgeBase;
import edu.arizona.biosemantics.common.ling.know.lib.InMemoryGlossary;
import edu.arizona.biosemantics.common.ling.know.lib.WordNetPOSKnowledgeBase;
import edu.arizona.biosemantics.common.ling.transform.IInflector;
import edu.arizona.biosemantics.common.ling.transform.ITokenizer;
import edu.arizona.biosemantics.common.ling.transform.lib.SomeInflector;
import edu.arizona.biosemantics.common.ling.transform.lib.WhitespaceTokenizer;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.matrixgeneration.CLIMain;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.model.TermCategory;
import edu.arizona.biosemantics.oto.model.TermSynonym;
import edu.arizona.biosemantics.oto.model.lite.Decision;
import edu.arizona.biosemantics.oto.model.lite.Download;
import edu.arizona.biosemantics.oto.model.lite.Synonym;
import edu.arizona.biosemantics.semanticmarkup.enhance.know.KnowsSynonyms.SynonymSet;
import edu.arizona.biosemantics.semanticmarkup.enhance.know.lib.CSVKnowsPartOf;
import edu.arizona.biosemantics.semanticmarkup.enhance.know.lib.CSVKnowsSynonyms;
import edu.arizona.biosemantics.semanticmarkup.enhance.run.Run;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.CollapseBiologicalEntityToName;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.RemoveNonSpecificBiologicalEntitiesByBackwardConnectors;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.RemoveNonSpecificBiologicalEntitiesByForwardConnectors;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.RemoveNonSpecificBiologicalEntitiesByPassedParents;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.RemoveNonSpecificBiologicalEntitiesByRelations;
import edu.arizona.biosemantics.semanticmarkup.enhance.transform.SimpleRemoveSynonyms;

public class InJvmMatrixGeneration implements MatrixGeneration {
	
	private String inputDir;
	private String taxonGroup;
	private String outputFile;	
	private boolean inheritValues;
	private boolean generateAbsentPresent;
	private boolean inferCharactersFromOntologies;
	private String inputOntology;
	private String termReviewSynonyms;
	private String termReviewTermCategorization;
	
	private boolean executedSuccessfully = false;
	private String tempDir;
	
	public InJvmMatrixGeneration(String inputDir, String inputOntology, String termReviewTermCategorization, String termReviewSynonyms,
			String taxonGroup, String outputFile, boolean inheritValues, boolean generateAbsentPresent, boolean inferCharactersFromOntologies, String tempDir) {
		this.inputDir = inputDir;
		this.inputOntology = inputOntology;
		this.termReviewTermCategorization = termReviewTermCategorization;
		this.termReviewSynonyms = termReviewSynonyms;
		this.taxonGroup = taxonGroup;
		this.outputFile = outputFile;
		this.inheritValues = inheritValues;
		this.generateAbsentPresent = generateAbsentPresent;
		this.inferCharactersFromOntologies = inferCharactersFromOntologies;
		this.tempDir = tempDir;
	}
	
	@Override
	public Void call() throws MatrixGenerationException {
		try {
			Enhance enhance = new Enhance(TaxonGroup.valueOf(taxonGroup), inputDir, tempDir, termReviewTermCategorization, termReviewSynonyms);
			enhance.run();
			
			List<String> argList = new LinkedList<String>();
			addArg(argList, "input", tempDir);
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

	/*public static void main(String[] args) throws Exception {
		//MatrixGeneration mg = new MatrixGeneration("C:/test/users/1070/input_2", "C:/test/temp/matrixGeneration/124/Matrix.mx");
		InJvmMatrixGeneration mg = new InJvmMatrixGeneration("C:/test/Test_mmm", "", "Plant", "C:/test/Test_mmm.mx", true, true, true);
		mg.call();
		
	}*/
}
