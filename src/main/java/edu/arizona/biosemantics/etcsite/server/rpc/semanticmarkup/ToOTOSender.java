package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.lucene.queryparser.classic.ParseException;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
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
import edu.arizona.biosemantics.oto.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.model.TermContext;
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
	
	private ContextService contextService;
	
	@Inject
	public ToOTOSender(ContextService contextService) {
		this.contextService = contextService;
	}
	
	public void send(Task task, SemanticMarkupConfiguration config, User user, Collection collection) throws Exception {
		try(OTOClient otoClient = new OTOClient(edu.arizona.biosemantics.etcsite.server.Configuration.otoUrl)) {
			otoClient.open();
			String datasetName = createDataSet(otoClient, config, user, task);		
			createTerms(datasetName, otoClient, config, user, collection);
			createCategorizations(datasetName, otoClient, user, collection);
		} catch (InterruptedException | ExecutionException e) {
			log(LogLevel.ERROR, "Could not send terms to OTO", e);
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

	private void createTerms(String datasetName, OTOClient otoClient, SemanticMarkupConfiguration config, User user, Collection collection) throws InterruptedException, ExecutionException, ParseException, IOException {
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
				
				ArrayList<edu.arizona.biosemantics.oto.model.Term> changedTerms = 
						new ArrayList<edu.arizona.biosemantics.oto.model.Term>();
				for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
					List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
					edu.arizona.biosemantics.oto.model.Term term = new edu.arizona.biosemantics.oto.model.Term();
					term.setIsAdditional(false);
					term.setHasSyn(false);
					term.setTerm(mainTerm.getTerm());
					changedTerms.add(term);
					
					ArrayList<edu.arizona.biosemantics.oto.model.Term> synoymTerms = 
							new ArrayList<edu.arizona.biosemantics.oto.model.Term>();
					for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
						edu.arizona.biosemantics.oto.model.Term synonymTerm = new edu.arizona.biosemantics.oto.model.Term();
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
				
				ArrayList<edu.arizona.biosemantics.oto.model.Term> changedTerms = 
						new ArrayList<edu.arizona.biosemantics.oto.model.Term>();
				for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
					List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
					if(!termsSynonyms.isEmpty()) {
						String relatedTerms = "";
						for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
							edu.arizona.biosemantics.oto.model.Term oldTerm = new edu.arizona.biosemantics.oto.model.Term();
							oldTerm.setAdditional(true);
							oldTerm.setHasSyn(false);
							oldTerm.setRelatedTerms("synonym of '" + mainTerm.getTerm() + "'");
							oldTerm.setTerm(synonym.getTerm());
							changedTerms.add(oldTerm);
							relatedTerms += "'" + synonym.getTerm() + "',";
						}
						
						edu.arizona.biosemantics.oto.model.Term term = new edu.arizona.biosemantics.oto.model.Term();
						term.setIsAdditional(false);
						term.setHasSyn(true);
						term.setTerm(mainTerm.getTerm());
						term.setRelatedTerms(relatedTerms.substring(0, relatedTerms.length() - 1));
						
						ArrayList<edu.arizona.biosemantics.oto.model.Term> synoymTerms = new ArrayList<edu.arizona.biosemantics.oto.model.Term>();
						for(edu.arizona.biosemantics.oto2.oto.shared.model.Term synonym : termsSynonyms) {
							edu.arizona.biosemantics.oto.model.Term synonymTerm = new edu.arizona.biosemantics.oto.model.Term();
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


	private void addTermContext(List<TermContext> termContexts, Collection collection, Term term) throws ParseException, IOException {
		List<TypedContext> contexts = contextService.getContexts(collection, term);
		if(contexts.isEmpty())
			termContexts.add(new TermContext(term.getTerm(), ""));
		for(TypedContext context : contexts) 
			termContexts.add(new TermContext(term.getTerm(), context.getText()));
	}
	
	public static void main(String[] args) throws Exception {
		ToOTOSender sender = new ToOTOSender(null);
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
	}
	
}
