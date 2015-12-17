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
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.matrixgeneration.CLIMain;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.model.TermCategory;
import edu.arizona.biosemantics.oto.model.TermSynonym;
import edu.arizona.biosemantics.oto.model.lite.Decision;
import edu.arizona.biosemantics.oto.model.lite.Download;
import edu.arizona.biosemantics.oto.model.lite.Synonym;
import edu.arizona.biosemantics.semanticmarkup.enhance.config.Configuration;
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
	
	private boolean executedSuccessfully = false;
	
	public InJvmMatrixGeneration(String inputDir, String inputOntology, String taxonGroup, String outputFile, boolean inheritValues, 
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
		//enhance part
		TaxonGroup taxonGroup = TaxonGroup.valueFromDisplayName(this.taxonGroup);	
		ITokenizer tokenizer = new WhitespaceTokenizer();
		WordNetPOSKnowledgeBase wordNetPOSKnowledgeBase = new WordNetPOSKnowledgeBase(Configuration.wordNetDirectory, false);
		SingularPluralProvider singularPluralProvider = new SingularPluralProvider();
		IInflector inflector = new SomeInflector(wordNetPOSKnowledgeBase, singularPluralProvider.getSingulars(), singularPluralProvider.getPlurals());
		Map<String, String> renames = new HashMap<String, String>();
		renames.put("count", "quantity");
		renames.put("atypical_count", "atypical_quantity");
		renames.put("color", "coloration");
		IGlossary glossary = new InMemoryGlossary();
		initGlossary(glossary, inflector, taxonGroup);
		Set<String> lifeStyles = glossary.getWordsInCategory("life_style");
		lifeStyles.addAll(glossary.getWordsInCategory("growth_form"));
		Set<String> durations = glossary.getWordsInCategory("duration");
		String negWords = "no|not|never";
		String advModifiers = "at least|at first|at times";
		String stopWords = "a|about|above|across|after|along|also|although|amp|an|and|are|as|at|be|because|become|becomes|becoming|been|before|being|"
				+ "beneath|between|beyond|but|by|ca|can|could|did|do|does|doing|done|for|from|had|has|have|hence|here|how|however|if|in|into|inside|inward|is|it|its|"
				+ "may|might|more|most|near|of|off|on|onto|or|out|outside|outward|over|should|so|than|that|the|then|there|these|this|those|throughout|"
				+ "to|toward|towards|up|upward|was|were|what|when|where|which|why|with|within|without|would";
		String units = "(?:(?:pm|cm|mm|dm|ft|m|meters|meter|micro_m|micro-m|microns|micron|unes|µm|μm|um|centimeters|centimeter|millimeters|millimeter|transdiameters|transdiameter)[23]?)"; //squared or cubed
		ICharacterKnowledgeBase characterKnowledgeBase = new GlossaryBasedCharacterKnowledgeBase(glossary, 
				negWords, advModifiers, stopWords, units, inflector);
		Set<String> possessionTerms = getWordSet("with|has|have|having|possess|possessing|consist_of");
		
		CSVReader reader = new CSVReader(new FileReader("C:\\Users\\rodenhausen\\Desktop\\test-enhance\\"
				+ "Gordon_complexity_term_review\\category_mainterm_synonymterm-task-Gordon_complexity.csv"));
		List<String[]> lines = reader.readAll();
		int i=0;
		final Map<String, SynonymSet> synonymSetsMap = new HashMap<String, SynonymSet>();
		for(String[] line : lines) {
			String preferredTerm = line[1];
			String synonym = line[2];
			if(!synonymSetsMap.containsKey(preferredTerm)) 
				synonymSetsMap.put(preferredTerm, new SynonymSet(preferredTerm, new HashSet<String>()));
			synonymSetsMap.get(preferredTerm).getSynonyms().add(synonym);
		}	
		
		/*List<KnowsSynonyms> hasBiologicalEntitySynonymsList = new LinkedList<KnowsSynonyms>();
		List<KnowsSynonyms> hasCharacterSynonymsList = new LinkedList<KnowsSynonyms>();
		hasBiologicalEntitySynonymsList.add(new CSVKnowsSynonyms() {
			@Override
			public Set<SynonymSet> getSynonyms(String term) {
				Set<SynonymSet> result = new HashSet<SynonymSet>();
				for(SynonymSet synonymSet : synonymSetsMap.values()) {
					if(synonymSet.getPreferredTerm().equals(term) || synonymSet.getSynonyms().contains(term)) 
						result.add(synonymSet);
				}
				if(result.isEmpty())
					result.add(new SynonymSet(term, new HashSet<String>()));
				return result;
			}
		});*/
		
		
		Run run = new Run();
		//AbstractTransformer transformer = new RemoveSynonyms(hasBiologicalEntitySynonymsList, hasBiologicalEntitySynonymsList);
		//AbstractTransformer transformer = new CreateRelationFromCharacterConstraint(new KeyWordBasedKnowsCharacterConstraintType(wordNetPOSKnowledgeBase), inflector);
		//AbstractTransformer transformer = new MoveRelationToBiologicalEntityConstraint();//new KeyWordBasedKnowsCharacterConstraintType(wordNetPOSKnowledgeBase), inflector);
		//AbstractTransformer transformer = new MoveNegationOrAdverbBiologicalEntityConstraint(wordNetPOSKnowledgeBase);
		/*AbstractTransformer transformer = new RemoveNonSpecificBiologicalEntitiesByRelations(new KnowsPartOf() {
			@Override
			public boolean isPartOf(String part, String parent) {
				if(part.equals("apex") && parent.equals("leaf")) {
					return true;
				}
				if(part.equals("base") && parent.equals("fruit")) {
					return true;
				}
				if(part.equals("base") && parent.equals("petal")) {
					return true;
				}
				return false;
			}
		}, tokenizer, new CollapseBiologicalEntityToName());*/
		
		//AbstractTransformer transformer1 = new MoveCharacterToStructureConstraint();
		//AbstractTransformer transformer2 = new ReplaceNegationCharacterByNegationOrAbsence();
		
		/*AbstractTransformer transformer = new MoveModifierCharactersToBiologicalEntityConstraint(tokenizer, new KnowsEntityExistence() {
			@Override
			public boolean isExistsEntity(String name) {
				if(name.equals("red leaf")) {
					return true;
				}
				return false;
			}
		});*/
	
		CSVKnowsSynonyms csvKnowsSynonyms = new CSVKnowsSynonyms(inflector);
		RemoveNonSpecificBiologicalEntitiesByRelations transformer1 = new RemoveNonSpecificBiologicalEntitiesByRelations(
				new CSVKnowsPartOf(csvKnowsSynonyms, inflector), csvKnowsSynonyms,
				tokenizer, new CollapseBiologicalEntityToName());
		RemoveNonSpecificBiologicalEntitiesByBackwardConnectors transformer2 = new RemoveNonSpecificBiologicalEntitiesByBackwardConnectors(
				new CSVKnowsPartOf(csvKnowsSynonyms, inflector), csvKnowsSynonyms, 
				tokenizer, new CollapseBiologicalEntityToName());
		RemoveNonSpecificBiologicalEntitiesByForwardConnectors transformer3 = new RemoveNonSpecificBiologicalEntitiesByForwardConnectors(
				new CSVKnowsPartOf(csvKnowsSynonyms, inflector), csvKnowsSynonyms,
				tokenizer, new CollapseBiologicalEntityToName());
		RemoveNonSpecificBiologicalEntitiesByPassedParents transformer4 = new RemoveNonSpecificBiologicalEntitiesByPassedParents(
				new CSVKnowsPartOf(csvKnowsSynonyms, inflector), 
				csvKnowsSynonyms, tokenizer, new CollapseBiologicalEntityToName(), inflector);
		//RemoveNonSpecificBiologicalEntitiesByCollections removeByCollections = new RemoveNonSpecificBiologicalEntitiesByCollections(
		//		new CSVKnowsPartOf(csvKnowsSynonyms, inflector), csvKnowsSynonyms, new CSVKnowsClassHierarchy(inflector), 
		//		tokenizer, new CollapseBiologicalEntityToName(), inflector);
		
		run.addTransformer(new SimpleRemoveSynonyms(csvKnowsSynonyms));
		run.addTransformer(transformer1);
		run.addTransformer(transformer2);
		run.addTransformer(transformer3);
		run.addTransformer(transformer4);
		//run.addTransformer(removeByCollections);
		
		//run.addTransformer(transformer1);
		//run.addTransformer(transformer2);
		/*
		AbstractTransformer transformer = new CollapseCharacterToValue();
		run.addTransformer(new RemoveOrphanRelations());
		run.addTransformer(new RemoveDuplicateValues());
		run.addTransformer(new CollapseBiologicalEntityToName());
		run.addTransformer(new CollapseCharacterToValue());
		run.addTransformer(new CollapseBiologicalEntities());
		run.addTransformer(new CollapseCharacters()); 
		*/
		/*
		run.addTransformer(new SplitCompoundBiologicalEntity(inflector));
		run.addTransformer(new SplitCompoundBiologicalEntitiesCharacters(inflector));
		run.addTransformer(new RemoveUselessWholeOrganism());
		run.addTransformer(new RemoveUselessCharacterConstraint());
		run.addTransformer(new RenameCharacter(renames));
		run.addTransformer(new MoveCharacterToStructureConstraint());
		run.addTransformer(new MoveNegationCharacterToBiologicalEntityConstraint());
		run.addTransformer(new MoveNegationOrAdverbBiologicalEntityConstraint(wordNetPOSKnowledgeBase));
		run.addTransformer(new MoveCharactersToAlternativeParent());
		run.addTransformer(new ReplaceTaxonNameByWholeOrganism());
		run.addTransformer(new CreateOrPopulateWholeOrganism(lifeStyles, "growth_form"));
		run.addTransformer(new CreateOrPopulateWholeOrganism(durations, "duration"));
		run.addTransformer(new StandardizeQuantityPresence());
		run.addTransformer(new StandardizeCount());
		run.addTransformer(new SortBiologicalEntityNameWithDistanceCharacter());
		run.addTransformer(new OrderBiologicalEntityConstraint());
		run.addTransformer(new StandardizeStructureName(characterKnowledgeBase, possessionTerms));
		run.addTransformer(new StandardizeTerminology(characterKnowledgeBase));
		
		run.addTransformer(new RemoveOrphanRelations());
		run.addTransformer(new RemoveDuplicateValues());
		run.addTransformer(new CollapseBiologicalEntityToName());
		run.addTransformer(new CollapseCharacterToValue());
		run.addTransformer(new CollapseBiologicalEntities());
		run.addTransformer(new CollapseCharacters());
		
		
		*/
		
		//run.run(new File("C:\\Users\\rodenhausen\\Desktop\\test-enhance\\selection_parsed2"), new File("C:\\Users\\rodenhausen\\Desktop\\test-enhance\\selection_parsed2_out_" + transformer.getClass().getSimpleName()));
		run.run(new File("in"), new File("out"));
		
		
		
		//matrix generation part
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
		InJvmMatrixGeneration mg = new InJvmMatrixGeneration("C:/test/Test_mmm", "", "Plant", "C:/test/Test_mmm.mx", true, true, true);
		mg.call();
		
	}
	
	
	private static Set<String> getWordSet(String regexString) {
		Set<String> set = new HashSet<String>();
		String[] wordsArray = regexString.split("\\|");
		for (String word : wordsArray)
			set.add(word.toLowerCase().trim());
		return set;
	}

	private static void initGlossary(IGlossary glossary, IInflector inflector, TaxonGroup taxonGroup) throws IOException {
		OTOClient otoClient = new OTOClient("http://biosemantics.arizona.edu:8080/OTO");
		GlossaryDownload glossaryDownload = new GlossaryDownload();		
		String glossaryVersion = "latest";
		otoClient.open();
		Future<GlossaryDownload> futureGlossaryDownload = otoClient.getGlossaryDownload(taxonGroup.getDisplayName(), glossaryVersion);
		
		try {
			glossaryDownload = futureGlossaryDownload.get();
		} catch (Exception e) {
			otoClient.close();
			e.printStackTrace();
		}
		otoClient.close();
				
		//add the syn set of the glossary
		HashSet<Term> gsyns = new HashSet<Term>();
		for(TermSynonym termSyn: glossaryDownload.getTermSynonyms()){

			//if(termSyn.getCategory().compareTo("structure")==0){
			if(termSyn.getCategory().matches("structure|taxon_name|substance")) {
				//take care of singular and plural forms
				String syns = ""; 
				String synp = "";
				String terms = "";
				String termp = "";
				if(inflector.isPlural(termSyn.getSynonym().replaceAll("_",  "-"))){ //must convert _ to -, as matching entity phrases will be converted from leg iii to leg-iii in the sentence.
					synp = termSyn.getSynonym().replaceAll("_",  "-");
					syns = inflector.getSingular(synp);					
				}else{
					syns = termSyn.getSynonym().replaceAll("_",  "-");
					synp = inflector.getPlural(syns);
				}

				if(inflector.isPlural(termSyn.getTerm().replaceAll("_",  "-"))){
					termp = termSyn.getTerm().replaceAll("_",  "-");
					terms = inflector.getSingular(termp);					
				}else{
					terms = termSyn.getTerm().replaceAll("_",  "-");
					termp = inflector.getPlural(terms);
				}
				glossary.addSynonym(syns, termSyn.getCategory(), terms);
				glossary.addSynonym(synp, termSyn.getCategory(), termp);
				gsyns.add(new Term(syns, termSyn.getCategory()));
				gsyns.add(new Term(synp, termSyn.getCategory()));
			}else{
				//glossary.addSynonym(termSyn.getSynonym().replaceAll("_",  "-"), "arrangement", termSyn.getTerm());
				glossary.addSynonym(termSyn.getSynonym().replaceAll("_",  "-"), termSyn.getCategory(), termSyn.getTerm());
				gsyns.add(new Term(termSyn.getSynonym().replaceAll("_",  "-"), termSyn.getCategory()));
				//gsyns.add(new Term(termSyn.getSynonym().replaceAll("_",  "-"), "arrangement"));
			}
		}

		//the glossary, excluding gsyns
		for(TermCategory termCategory : glossaryDownload.getTermCategories()) {
			if(!gsyns.contains(new Term(termCategory.getTerm().replaceAll("_", "-"), termCategory.getCategory())))
				glossary.addEntry(termCategory.getTerm().replaceAll("_", "-"), termCategory.getCategory()); //primocane_foliage =>primocane-foliage Hong 3/2014
		}	
		
		
		List<Synonym> synonyms = new LinkedList<Synonym>();
		CSVReader reader = new CSVReader(new FileReader("C:\\Users\\rodenhausen\\Desktop\\test-enhance\\"
				+ "Gordon_complexity_term_review\\category_mainterm_synonymterm-task-Gordon_complexity.csv"));
		List<String[]> lines = reader.readAll();
		int i=0;
		Set<String> hasSynonym = new HashSet<String>();
		for(String[] line : lines) {
			synonyms.add(new Synonym(String.valueOf(i), line[1], line[0], line[2]));
			hasSynonym.add(line[1]);
		}	
		
		reader = new CSVReader(new FileReader("C:\\Users\\rodenhausen\\Desktop\\test-enhance\\"
				+ "Gordon_complexity_term_review\\category_term-task-Gordon_complexity.csv"));
		lines = reader.readAll();
		List<Decision> decisions = new LinkedList<Decision>();
		i=0;
		for(String[] line : lines) {
			decisions.add(new Decision(String.valueOf(i), line[1], line[0], hasSynonym.contains(line[1]), ""));
		}

		Download download = new Download(true, decisions, synonyms);
		
		
		//add syn set of term_category
		HashSet<Term> dsyns = new HashSet<Term>();
		if(download != null) {
			for(Synonym termSyn: download.getSynonyms()){
				//Hong TODO need to add category info to synonym entry in OTOLite
				//if(termSyn.getCategory().compareTo("structure")==0){
				if(termSyn.getCategory().matches("structure|taxon_name|substance")){
					//take care of singular and plural forms
					String syns = ""; 
					String synp = "";
					String terms = "";
					String termp = "";
					if(inflector.isPlural(termSyn.getSynonym().replaceAll("_",  "-"))){
						synp = termSyn.getSynonym().replaceAll("_",  "-");
						syns = inflector.getSingular(synp);					
					}else{
						syns = termSyn.getSynonym().replaceAll("_",  "-");
						synp = inflector.getPlural(syns);
					}

					if(inflector.isPlural(termSyn.getTerm().replaceAll("_",  "-"))){
						termp = termSyn.getTerm().replaceAll("_",  "-");
						terms = inflector.getSingular(termp);					
					}else{
						terms = termSyn.getTerm().replaceAll("_",  "-");
						termp = inflector.getPlural(terms);
					}
					//glossary.addSynonym(syns, termSyn.getCategory(), terms);
					//glossary.addSynonym(synp, termSyn.getCategory(), termp);
					//dsyns.add(new Term(syns, termSyn.getCategory());
					//dsyns.add(new Term(synp, termSyn.getCategory());
					glossary.addSynonym(syns, termSyn.getCategory(), terms);
					glossary.addSynonym(synp,termSyn.getCategory(), termp);
					dsyns.add(new Term(syns, termSyn.getCategory()));
					dsyns.add(new Term(synp, termSyn.getCategory()));
				}else{//forking_1 and forking are syns 5/5/14 hong test, shouldn't _1 have already been removed?
					glossary.addSynonym(termSyn.getSynonym().replaceAll("_",  "-"), termSyn.getCategory(), termSyn.getTerm());
					dsyns.add(new Term(termSyn.getSynonym().replaceAll("_",  "-"), termSyn.getCategory()));
				}					
			}

			//term_category from OTO, excluding dsyns
			for(Decision decision : download.getDecisions()) {
				if(!dsyns.contains(new Term(decision.getTerm().replaceAll("_",  "-"), decision.getCategory())))//calyx_tube => calyx-tube
					glossary.addEntry(decision.getTerm().replaceAll("_",  "-"), decision.getCategory());  
			}
		}
	}
}
