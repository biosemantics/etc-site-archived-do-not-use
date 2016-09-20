package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jdom2.JDOMException;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.MarkupResultReader.BiologicalEntity;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.MarkupResultReader.Character;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.model.CategorizeTerms;
import edu.arizona.biosemantics.oto.model.CategoryBean;
import edu.arizona.biosemantics.oto.model.CreateDataset;
import edu.arizona.biosemantics.oto.model.DecisionHolder;
import edu.arizona.biosemantics.oto.model.GroupTerms;
import edu.arizona.biosemantics.oto.model.Order;
import edu.arizona.biosemantics.oto.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.model.Term;
import edu.arizona.biosemantics.oto.model.TermContext;
import edu.arizona.biosemantics.oto.model.TermOrder;
import edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.stanford.nlp.util.StringUtils;

public class ToOTOSender2 {

	//until there is a better API on OTO's end hardcoded here
	private Set<String> otoCategories = new HashSet<String>(Arrays.asList(new String[] { 
			"architecture",
			"arrangement",
			"behaviour",
			"character",
			"coloration",
			"condition",
			"count",
			"course",
			"dehiscence",
			"density",
			"depth",
			"derivation",
			"development",
			"duration",
			"external_texture",
			"exudation",
			"fixation",
			"fragility",
			"fusion",
			"germination",
			"habit",
			"height",
			"internal_texture",
			"length",
			"life_stage",
			"life_style",
			"location",
			"maturation",
			"nutrition",
			"odor",
			"organ",
			"orientation",
			"pattern",
			"position",
			"prominence",
			"reflectance",
			"relief",
			"reproduction",
			"season",
			"shape",
			"size",
			"structure",
			"taste",
			"taxon_name",
			"variability",
			"ventation",
			"vernation",
			"volume",
			"width"
			}));
	
	private ContextService contextService;

	private MarkupResultReader markupResultReader;
	
	@Inject
	public ToOTOSender2(ContextService contextService, MarkupResultReader markupResultReader) {
		this.contextService = contextService;
		this.markupResultReader = markupResultReader;
	}
	
	public void send(Task task, SemanticMarkupConfiguration config, User user, Collection collection) throws Exception {
		try(OTOClient otoClient = new OTOClient(edu.arizona.biosemantics.etcsite.server.Configuration.otoUrl)) {
			otoClient.open();
			String datasetName = createDataSet(otoClient, config, user, task);	
			createGrouping(datasetName, otoClient, config, user);
			createHierarchy(datasetName, otoClient, config, user);
			createOrders(datasetName, otoClient, config, user, collection);
		} catch (InterruptedException | ExecutionException e) {
			log(LogLevel.ERROR, "Could not send terms to OTO", e);
			throw new Exception("Could not send terms to OTO.");
		}
	}
	
	private void createHierarchy(String datasetName, OTOClient otoClient, SemanticMarkupConfiguration config, User user) throws JDOMException, IOException, InterruptedException, ExecutionException {
		File outputDir = new File(config.getOutput());
		List<BiologicalEntity> entities = markupResultReader.getBiologicalEntities(outputDir);
		List<TermContext> termContexts = new LinkedList<TermContext>();
		for(BiologicalEntity entity : entities) {
			if(entity.hasConstraint()) {
				
				//constraint only
				String term = entity.getConstraint();
				List<String> sentences = markupResultReader.getSentences(outputDir, term);
				for(String sentence : sentences) 
					termContexts.add(new TermContext(term, sentence));
				if(sentences.isEmpty())
					termContexts.add(new TermContext(term, ""));
				
				//constraint + entity
				term = entity.hasConstraint() ? entity.getConstraint() + " " + entity.getName() : entity.getName();
				sentences = markupResultReader.getSentences(outputDir, term);
				for(String sentence : sentences) 
					termContexts.add(new TermContext(term, sentence));
				if(sentences.isEmpty())
					termContexts.add(new TermContext(term, ""));
			} else {
				//term only
				String term = entity.getName();
				List<String> sentences = markupResultReader.getSentences(outputDir, term);
				for(String sentence : sentences) 
					termContexts.add(new TermContext(term, sentence));
				if(sentences.isEmpty())
					termContexts.add(new TermContext(term, ""));
			}
		}
		StructureHierarchy structureHierarchy = new StructureHierarchy(termContexts, user.getOtoAuthenticationToken(), false);
		otoClient.postStructureHierarchy(datasetName, structureHierarchy).get();
		
	}

	private void createGrouping(String datasetName, OTOClient otoClient, SemanticMarkupConfiguration config, User user) throws JDOMException, IOException, InterruptedException, ExecutionException {
		File outputDir = new File(config.getOutput());
		List<BiologicalEntity> entities = markupResultReader.getBiologicalEntities(outputDir);
		List<Character> characters = markupResultReader.getCharacters(outputDir, false);
		List<TermContext> termContexts = new LinkedList<TermContext>();
		Map<String, Set<String>> terms = new HashMap<String, Set<String>>();
		terms.put("structure", new HashSet<String>());
		for(BiologicalEntity entity : entities) {
			terms.get("structure").add(entity.getName());
		}
		for(Character character : characters) {
			String categoriesString = character.getCategory();
			String[] categories = categoriesString.split("_or_");
			for(String category : categories) {
				if(!terms.containsKey(category))
					terms.put(category, new HashSet<String>());
				terms.get(category).add(character.getValue());
			}
		}
		
		for(String category : terms.keySet()) {
			for(String term : terms.get(category)) {
				List<String> sentences = markupResultReader.getSentences(outputDir, term);
				for(String sentence : sentences) 
					termContexts.add(new TermContext(term, sentence));
				if(sentences.isEmpty())
					termContexts.add(new TermContext(term, ""));
			}
		}
		
		GroupTerms groupTerms = new GroupTerms(termContexts, user.getOtoAuthenticationToken(), false);
		otoClient.postGroupTerms(datasetName, groupTerms).get();
		
		//first step
		CategorizeTerms categorizeTerms = new CategorizeTerms();
		categorizeTerms.setAuthenticationToken(user.getOtoAuthenticationToken());
		
		DecisionHolder decisionHolder = new DecisionHolder();
		ArrayList<CategoryBean> regularCategories = new ArrayList<CategoryBean>();
		
		for(String category : terms.keySet()) {
			if(otoCategories.contains(category.toLowerCase())) {
				CategoryBean categoryBean = new CategoryBean();
				categoryBean.setName(category);
				
				ArrayList<Term> changedTerms = 
						new ArrayList<Term>();
				for(String term : terms.get(category)) {
					Term otoTerm = new Term();
					otoTerm.setIsAdditional(false);
					otoTerm.setHasSyn(false);
					otoTerm.setTerm(term);
					changedTerms.add(otoTerm);
				}
				if(!changedTerms.isEmpty()) {
					regularCategories.add(categoryBean);
					categoryBean.setChanged_terms(changedTerms);
				}
			}
		}
		
		decisionHolder.setRegular_categories(regularCategories);
		categorizeTerms.setDecisionHolder(decisionHolder);
		otoClient.postGroupTermsCategorization(datasetName, categorizeTerms).get();
	}

	private void createOrders(String datasetName, OTOClient otoClient, SemanticMarkupConfiguration config, User user, Collection collection) throws JDOMException, IOException, InterruptedException, ExecutionException {
		Set<String> collectionCharacterStates = new HashSet<String>();
		for(Label label : collection.getLabels()) {
			for(edu.arizona.biosemantics.oto2.oto.shared.model.Term term : label.getTerms()) {
				collectionCharacterStates.add(term.getTerm());
			}
		}
		
		File outputDir = new File(config.getOutput());
		List<Character> rangeCharacters = markupResultReader.getRangeValueCharacters(outputDir, false);
		Map<String, Order> ordersMap = new HashMap<String, Order>();
		for(Character rangeCharacter : rangeCharacters) {
			String categoriesString = rangeCharacter.getCategory();
			String[] categories = categoriesString.split("_or_");
			for(String category : categories) {
				if(!ordersMap.containsKey(category))
					ordersMap.put(category, new Order(category, new ArrayList<String>()));
				String value = rangeCharacter.getValue();
				Set<String> values = normalize(value, collectionCharacterStates);
				for(String v : values) 
					ordersMap.get(category).getTerms().add(v);
			}
		}
		
		//deduplicate
		for(Order order : ordersMap.values()) 
			order.setTerms(new ArrayList<String>(new HashSet<String>(order.getTerms())));
		TermOrder termOrder = new TermOrder(new ArrayList<Order>(ordersMap.values()), user.getOtoAuthenticationToken(), false);
		otoClient.postTermOrder(datasetName, termOrder).get();
	}

	private Set<String> normalize(String value, Set<String> collectionCharacterStates) {		
		Set<String> result = new HashSet<String>();
		for(String illegalModifier : Configuration.illegalOrderModifiers)
			if(value.startsWith(illegalModifier))
				value = value.replace(illegalModifier, "").trim();
		if(value.endsWith(" or")) {
			value = value.substring(0, value.length() - 3).trim();
		}
		String[] orParts = value.split(" or ");
		for(String orPart : orParts) {
			String[] terms = orPart.split(" ");
			List<String> remainingTerms = new LinkedList<String>();
			for(String term : terms) {
				if(!Configuration.illegalOrderModifiers.contains(term)) {
					remainingTerms.add(term);
				}
			}
			orPart = StringUtils.join(remainingTerms, " ");
			
			String[] toParts = orPart.split(" to ");
			if(toParts.length == 2) {
				result.addAll(getNormalizedToCharacterStates(toParts, collectionCharacterStates));				
			} else {
				result.add(orPart);	
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		String test = "more or less coriaceous";
		ToOTOSender2 toOTOSender2 = new ToOTOSender2(null, null);
		System.out.println("result: " + toOTOSender2.normalize(test, new HashSet<String>()));
	}

	private Set<String> getNormalizedToCharacterStates(String[] toParts, Set<String> collectionCharacterStates) {
		Set<String>  result = new HashSet<String>();
		String[] terms1 = toParts[0].split(" ");
		String[] terms2 = toParts[1].split(" ");
		
		for(String term : terms1) {
			if(collectionCharacterStates.contains(term)) {
				result.add(toParts[0]);
				result.add(toParts[1]);
				return result;
			}
		}
		
		List<String> characterStates = new LinkedList<String>();
		for(int j=terms2.length-1; j<=0; j--) {
			String term = terms2[j];
			if(collectionCharacterStates.contains(term)) {
				characterStates.add(0, term);
			} else {
				break;
			}
		}
		if(characterStates.isEmpty()) {
			result.add(toParts[0]);
			result.add(toParts[1]);
			return result;
		} else {
			List<String> terms1List = new LinkedList<String>(Arrays.asList(terms1));
			terms1List.addAll(characterStates);
			result.add(StringUtils.join(terms1List, " "));
			result.add(toParts[1]);
			return result;
		}
	}


	private String createDataSet(OTOClient otoClient, SemanticMarkupConfiguration config, User user, Task task) throws InterruptedException, ExecutionException {
		edu.arizona.biosemantics.common.biology.TaxonGroup taxonGroupEnum = 
				edu.arizona.biosemantics.common.biology.TaxonGroup.valueOf(config.getTaxonGroup().getName().toUpperCase());
		CreateDataset createDataset = new CreateDataset(task.getName(), taxonGroupEnum, user.getOtoAuthenticationToken());
		Future<String> datasetFuture = otoClient.postDataset(createDataset);
		return datasetFuture.get();
	}
	
	/*public static void main(String[] args) throws Exception {
		ToOTOSender2 sender = new ToOTOSender2(null, null);
		Task task = new Task();
		task.setName("my_name");
		SemanticMarkupConfiguration config = new SemanticMarkupConfiguration();
		TaxonGroup taxonGroup = new TaxonGroup(-1, "PLANT", null);
		config.setTaxonGroup(taxonGroup);
		User user = new User();
		user.setOtoAuthenticationToken("m0bMEBFatNZp1C3nv7Nx4g==");
		user.setOtoAccountEmail("test-thomas9@gmail.com");
		Collection collection = new Collection();
		sender.send(task, config, user, collection);
	}*/
	
}
