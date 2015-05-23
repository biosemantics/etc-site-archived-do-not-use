package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.CategorizeTerms;
import edu.arizona.biosemantics.oto.common.model.CategoryBean;
import edu.arizona.biosemantics.oto.common.model.CreateDataset;
import edu.arizona.biosemantics.oto.common.model.DecisionHolder;
import edu.arizona.biosemantics.oto.common.model.GroupTerms;
import edu.arizona.biosemantics.oto.common.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.common.model.TermContext;
import edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IContextService;

public class ToOTOSender {

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
	
	private IContextService contextService = new ContextService();
	
	public void send(Task task, SemanticMarkupConfiguration config, User user, Collection collection) throws Exception {
		try(OTOClient otoClient = new OTOClient(edu.arizona.biosemantics.etcsite.server.Configuration.otoUrl)) {
			otoClient.open();
			String datasetName = createDataSet(otoClient, config, user, task);		
			createTerms(datasetName, otoClient, config, user, collection);
			createCategorizations(datasetName, otoClient, user, collection);
		} catch (InterruptedException | ExecutionException e) {
			log(LogLevel.ERROR, "Couldnt' set description upon rename term", e);
			throw new Exception("Could not send terms to OTO.");
		}
	}	
	
	private String createDataSet(OTOClient otoClient, SemanticMarkupConfiguration config, User user, Task task) throws InterruptedException, ExecutionException {
		edu.arizona.biosemantics.common.biology.TaxonGroup taxonGroupEnum = 
				edu.arizona.biosemantics.common.biology.TaxonGroup.valueOf(config.getTaxonGroup().getName().toUpperCase());
		CreateDataset createDataset = new CreateDataset(task.getName(), taxonGroupEnum, user.getOtoAuthenticationToken());
		Future<String> datasetFuture = otoClient.postDataset(createDataset);
		return datasetFuture.get();
	}

	private void createTerms(String datasetName, OTOClient otoClient, SemanticMarkupConfiguration config, User user, Collection collection) throws InterruptedException, ExecutionException {
		List<TermContext> termContexts = new LinkedList<TermContext>();
		for(Bucket bucket : collection.getBuckets()) {
			for(Term term : bucket.getTerms()) {
				addTermContext(termContexts, collection, term);
			}
		}

		/*for(Label label : collection.getLabels()) {
			for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
				addTermContext(termContexts, collection, mainTerm);
				
				List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
				for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
					addTermContext(termContexts, collection, synonym);
				}
			}
		}*/
		GroupTerms groupTerms = new GroupTerms(termContexts, user.getOtoAuthenticationToken(), false);
		StructureHierarchy structureHierarchy = new StructureHierarchy(termContexts, user.getOtoAuthenticationToken(), false);
		
		otoClient.postGroupTerms(datasetName, groupTerms);
		otoClient.postStructureHierarchy(datasetName, structureHierarchy).get();
	}

	/**
	 * OTO does only allow synonym creation in two steps. First terms have all to be moved into the category.
	 * Then in second step synonym relation can be created.
	 * @param datasetName
	 * @param otoClient
	 * @param user 
	 * @param collection 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private void createCategorizations(String datasetName, OTOClient otoClient, User user, Collection collection) throws InterruptedException, ExecutionException {
		//first step
		CategorizeTerms categorizeTerms = new CategorizeTerms();
		categorizeTerms.setAuthenticationToken(user.getOtoAuthenticationToken());
		
		DecisionHolder decisionHolder = new DecisionHolder();
		ArrayList<CategoryBean> regularCategories = new ArrayList<CategoryBean>();
		
		for(Label label : collection.getLabels()) {
			if(otoCategories.contains(label.getName().toLowerCase())) {
				CategoryBean categoryBean = new CategoryBean();
				categoryBean.setName(label.getName());
				
				ArrayList<edu.arizona.biosemantics.oto.common.model.Term> changedTerms = 
						new ArrayList<edu.arizona.biosemantics.oto.common.model.Term>();
				for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
					List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
					edu.arizona.biosemantics.oto.common.model.Term term = new edu.arizona.biosemantics.oto.common.model.Term();
					term.setIsAdditional(false);
					term.setHasSyn(false);
					term.setTerm(mainTerm.getTerm());
					changedTerms.add(term);
					
					ArrayList<edu.arizona.biosemantics.oto.common.model.Term> synoymTerms = 
							new ArrayList<edu.arizona.biosemantics.oto.common.model.Term>();
					for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
						edu.arizona.biosemantics.oto.common.model.Term synonymTerm = new edu.arizona.biosemantics.oto.common.model.Term();
						synonymTerm.setIsAdditional(false);
						synonymTerm.setHasSyn(false);
						synonymTerm.setTerm(synonym.getTerm());
						changedTerms.add(synonymTerm);
					}
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
				
		//second step
		categorizeTerms = new CategorizeTerms();
		categorizeTerms.setAuthenticationToken(user.getOtoAuthenticationToken());
		decisionHolder = new DecisionHolder();
		regularCategories = new ArrayList<CategoryBean>();
		
		for(Label label : collection.getLabels()) {
			if(otoCategories.contains(label.getName().toLowerCase())) {
				CategoryBean categoryBean = new CategoryBean();
				categoryBean.setName(label.getName());
				
				ArrayList<edu.arizona.biosemantics.oto.common.model.Term> changedTerms = 
						new ArrayList<edu.arizona.biosemantics.oto.common.model.Term>();
				for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
					List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
					if(!termsSynonyms.isEmpty()) {
						String relatedTerms = "";
						for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
							edu.arizona.biosemantics.oto.common.model.Term oldTerm = new edu.arizona.biosemantics.oto.common.model.Term();
							oldTerm.setAdditional(true);
							oldTerm.setHasSyn(false);
							oldTerm.setRelatedTerms("synonym of '" + mainTerm.getTerm() + "'");
							oldTerm.setTerm(synonym.getTerm());
							changedTerms.add(oldTerm);
							relatedTerms += "'" + synonym.getTerm() + "',";
						}
						
						edu.arizona.biosemantics.oto.common.model.Term term = new edu.arizona.biosemantics.oto.common.model.Term();
						term.setIsAdditional(false);
						term.setHasSyn(true);
						term.setTerm(mainTerm.getTerm());
						term.setRelatedTerms(relatedTerms.substring(0, relatedTerms.length() - 1));
						
						ArrayList<edu.arizona.biosemantics.oto.common.model.Term> synoymTerms = new ArrayList<edu.arizona.biosemantics.oto.common.model.Term>();
						for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
							edu.arizona.biosemantics.oto.common.model.Term synonymTerm = new edu.arizona.biosemantics.oto.common.model.Term();
							synonymTerm.setIsAdditional(false);
							synonymTerm.setHasSyn(false);
							synonymTerm.setTerm(synonym.getTerm());
							synoymTerms.add(synonymTerm);
						}
						term.setSyns(synoymTerms);
						changedTerms.add(term);
					}
				}
				if(!changedTerms.isEmpty()) {
					categoryBean.setChanged_terms(changedTerms);
					regularCategories.add(categoryBean);
				}
			}
		}
		decisionHolder.setRegular_categories(regularCategories);
		categorizeTerms.setDecisionHolder(decisionHolder);
		otoClient.postGroupTermsCategorization(datasetName, categorizeTerms).get();
	}


	private void addTermContext(List<TermContext> termContexts, Collection collection, Term term) {
		List<TypedContext> contexts = contextService.getContexts(collection, term);
		if(contexts.isEmpty())
			termContexts.add(new TermContext(term.getTerm(), ""));
		for(TypedContext context : contexts) 
			termContexts.add(new TermContext(term.getTerm(), context.getText()));
	}
	
}
