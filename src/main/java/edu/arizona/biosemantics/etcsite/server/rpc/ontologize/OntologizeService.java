package edu.arizona.biosemantics.etcsite.server.rpc.ontologize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.ontology.graph.OntologyGraph;
import edu.arizona.biosemantics.common.ontology.graph.Reader;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.JavaZipper;
import edu.arizona.biosemantics.etcsite.server.Zipper;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.MarkupResultReader;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.MarkupResultReader.BiologicalEntity;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.ontologize.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.OntologizeException;
import edu.arizona.biosemantics.oto2.ontologize2.server.owl.Ontology;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidates;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.common.context.shared.Context;

public class OntologizeService extends RemoteServiceServlet implements IOntologizeService {
	
	private IFileService fileService;
	private IFileFormatService fileFormatService;
	private IFilePermissionService filePermissionService;
	private IFileAccessService fileAccessService;
	private Emailer emailer;
	private edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService collectionService;
	private edu.arizona.biosemantics.oto2.ontologize2.shared.IContextService contextService;
	private DAOManager daoManager;
	private MarkupResultReader markupResultReader;

	@Inject
	public OntologizeService(IFileService fileService, IFileFormatService fileFormatService, 
			IFilePermissionService filePermissionService, 
			IFileAccessService fileAccessService, 
			edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService collectionService,
			edu.arizona.biosemantics.oto2.ontologize2.shared.IContextService contextService,
			DAOManager daoManager, Emailer emailer, MarkupResultReader markupResultReader) {
		this.fileService = fileService;
		this.fileFormatService = fileFormatService;
		this.filePermissionService = filePermissionService;
		this.fileAccessService = fileAccessService;
		this.collectionService = collectionService;
		this.contextService = contextService;
		this.daoManager = daoManager;
		this.emailer = emailer;
		this.markupResultReader = markupResultReader;
	}
	
	@Override
	public Task startWithOntologyCreation(AuthenticationToken token, String taskName, String input, String taxonGroup) throws OntologizeException {
		Collection collection = createOntologizeCollection(token, taskName, input, 
				edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup));
		String tempOntology = getOntologyOWLFile(collection);
		boolean isShared = filePermissionService.isSharedFilePath(token.getUserId(), input);
		String inputFileName;
		try {
			inputFileName = fileService.getFileName(token, input);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException();
		}
		if(isShared) {
			String destination;
			try {
				destination = fileService.createDirectory(token, Configuration.fileBase + File.separator + token.getUserId(), 
						inputFileName, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new OntologizeException();
			}
			try {
				fileService.copyFiles(token, input, destination);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new OntologizeException();
			}
			input = destination;
		}
		
		OntologizeConfiguration config = new OntologizeConfiguration();
		config.setInput(input);	
		TaxonGroup group = daoManager.getTaxonGroupDAO().getTaxonGroup(taxonGroup);
		config.setTaxonGroup(group);
		config.setOntologyFile(tempOntology);
		config.setOntologizeCollectionId(collection.getId());
		config.setOntologizeCollectionSecret(collection.getSecret());
		config.setOutput(config.getInput() + "_output_by_OB_task_" + taskName);
		config = daoManager.getOntologizeConfigurationDAO().addOntologizeConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.ONTOLOGIZE;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getOntologizeTaskStage(TaskStageEnum.INPUT.toString());
		TinyUser user = daoManager.getUserDAO().getTinyUser(token.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);
		
		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getOntologizeTaskStage(TaskStageEnum.BUILD.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);

		try {
			fileService.setInUse(token, true, input, task);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException(task);
		}
		return task;
	}
	
	
	private String getOntologyOWLFile(Collection collection) {
		return edu.arizona.biosemantics.oto2.ontologize2.server.Configuration.collectionsDirectory 
		+ File.separator + collection.getId() + File.separator + "owl" + File.separator + collection.getId() + ".owl";
	}

	private Collection createOntologizeCollection(AuthenticationToken token, String name, String input, 
			edu.arizona.biosemantics.common.biology.TaxonGroup taxonGroup) throws OntologizeException {
		Collection collection = new Collection();
		collection.setName(name);
		collection.setTaxonGroup(taxonGroup);
		collection.add(getCandidates(input));
		
		Map<Ontology, edu.arizona.biosemantics.common.ontology.graph.OntologyGraph> graphs = 
				new HashMap<Ontology, edu.arizona.biosemantics.common.ontology.graph.OntologyGraph>();
		List<Ontology> relevantOntologies = Ontology.getRelevantOntologies(taxonGroup);
		for(Ontology ro : relevantOntologies) {
			Reader reader = new Reader(edu.arizona.biosemantics.oto2.ontologize2.server.Configuration.ontologyGraphs + 
					File.separator + ro.getName() + ".graph");
			try {
				edu.arizona.biosemantics.common.ontology.graph.OntologyGraph g = reader.read();
				graphs.put(ro, g);
			} catch (Exception e) {
				log(LogLevel.ERROR, "Could not read graph", e);
			}
		}
		
		Candidates candidates = collection.getCandidates();
		for(OntologyGraph g : graphs.values()) {
			for(Candidate candidate : candidates) {
				Set<edu.arizona.biosemantics.common.ontology.graph.OntologyGraph.Vertex> vertices = g.getVerticesByName(candidate.getText());
				if(vertices.size() == 1) {
					for(edu.arizona.biosemantics.common.ontology.graph.OntologyGraph.Edge.Type type : edu.arizona.biosemantics.common.ontology.graph.OntologyGraph.Edge.Type.values()) {
						List<edu.arizona.biosemantics.common.ontology.graph.OntologyGraph.Edge> relations = g.getOutRelations(vertices.iterator().next(), type);
						for(edu.arizona.biosemantics.common.ontology.graph.OntologyGraph.Edge relation : relations) {
							if(candidates.contains(relation.getDest().getName())) {
								try {
									collection.getGraph().addRelation(new Edge(new Vertex(candidate.getText()), 
											new Vertex(relation.getDest().getName()), Type.valueOf(type.toString()), Origin.IMPORT));
								} catch(Exception e) {
									log(LogLevel.ERROR, "Could not add relation", e);
								}
							}
						}
					}
				}
			}
		}
		
		try {
			collection = collectionService.insert(collection);
			contextService.insert(collection.getId(), collection.getSecret(), getContexts(token, input, collection));
		} catch (Exception e) {
			throw new OntologizeException();
		}
		return collection;
	}
	
	private List<Context> getContexts(AuthenticationToken token, String input, Collection collection) throws OntologizeException {
		List<Context> contexts = new LinkedList<Context>();
		List<String> files = new LinkedList<String>();
		try {
			files = fileService.getDirectoriesFiles(token, input);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException();
		}
		for(String file : files) {
			List<Description> descriptions = getDescriptions(token, input + File.separator + file);
			for(Description description : descriptions) {
				try {
					contexts.add(new Context(collection.getId(), getTaxonIdentification(token, input + File.separator + file), description.getContent()));
				} catch (PermissionDeniedException | GetFileContentFailedException e) {
					throw new OntologizeException();
				}
			}
		}
		return contexts;
	}

	private List<Candidate> getCandidates(String input) throws OntologizeException {
		File inputFile = new File(input);
		List<MarkupResultReader.BiologicalEntity> structures;
		List<MarkupResultReader.Character> characters;
		List<MarkupResultReader.Character> rangeValueCharacters;
		try {
			structures = markupResultReader.getBiologicalEntities(inputFile);
			characters = markupResultReader.getCharacters(inputFile, false);
			rangeValueCharacters = markupResultReader.getRangeValueCharacters(inputFile, false);
		} catch (JDOMException | IOException e) {
			log(LogLevel.ERROR, "Couldn't parse input", e);
			throw new OntologizeException(e);
		}
		
		List<Candidate> result = new LinkedList<Candidate>();					
		Set<String> containedStructures = new HashSet<String>();
		for(BiologicalEntity structure : structures) {
			String name = structure.hasConstraint() ? structure.getConstraint() + " " + structure.getName() : structure.getName();
			if(!containedStructures.contains(name + structure.getIri())) {
				result.add(new Candidate(name, "/material anatomical entity"));
				containedStructures.add(name);
			}
		}
		Set<String> containedCharacters = new HashSet<String>();
		for(MarkupResultReader.Character character : characters) {
			//filter comparison values such as "wider than long". "twice of leaf"
			if(character.getValue().split("\\W+").length < 3) {
				if(!containedCharacters.contains(character.getValue() + character.getCategory() + character.getIri())) {
					result.add(new Candidate(character.getValue(), "/quality/" + character.getCategory()));
					containedCharacters.add(character.getValue() + character.getCategory() + character.getIri());
				}
			}
		}
		return result;
	}

	public List<Description> getDescriptions(AuthenticationToken authenticationToken, String filePath) {	
		List<Description> descriptions = new LinkedList<Description>();
		String fileContent;
		try {
			fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		} catch (PermissionDeniedException | GetFileContentFailedException e) {
			return null;
		}
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(fileContent)) {
			Document doc;
			try {
				doc = sax.build(reader);
			} catch (JDOMException | IOException e) {
				return null;
			}
			
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("//description", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> descriptionElements = xp.evaluate(doc);
			if(descriptionElements != null) {
				StringBuilder textBuilder = new StringBuilder();
				for(Element descriptionElement : descriptionElements) {
					String type = descriptionElement.getAttributeValue("type");
					List<Element> statements = descriptionElement.getChildren("statement");
					for(Element statement : statements) {
						Element textElement = statement.getChild("text");
						textBuilder.append(textElement.getValue() + " ");
					}
					String text = textBuilder.toString();
					if(!text.isEmpty())
						text = text.substring(0, text.length() - 1);
					descriptions.add(new Description(text, type));
				}
			}
		}
		return descriptions;
	}

	private String getTaxonIdentification(AuthenticationToken authenticationToken, String filePath) throws PermissionDeniedException, GetFileContentFailedException {
		String fileContent = fileAccessService.getFileContent(authenticationToken, filePath);
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(fileContent)) {
			Document doc = null;
			try {
				doc = sax.build(reader);
			} catch (JDOMException | IOException e) {
				log(LogLevel.ERROR, "Could not build xml document", e);
			}
		
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> sourceXPath = xpfac.compile("/bio:treatment/meta/source", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));	
			XPathExpression<Element> taxonNameXPath = xpfac.compile("/bio:treatment/taxon_identification", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			
			if(doc != null) {
				List<Element> taxonIdentifications = taxonNameXPath.evaluate(doc);
				List<Element> taxonNames = new LinkedList<Element>();
				for(Element taxonIdentification : taxonIdentifications) {
					if(taxonIdentification.getAttributeValue("status").equalsIgnoreCase("accepted")) {
						taxonNames.addAll(taxonIdentification.getChildren("taxon_name"));
					}
				}
				List<Element> sources = sourceXPath.evaluate(doc);
				return createTaxonIdentification(sources.get(0), taxonNames);
			}
		}
		return null;
	}
	
	private String createTaxonIdentification(Element source, List<Element> taxonIdentifications) {
		String result = "";
		//StringBuilder taxonNameBuilder = new StringBuilder();
		
		//the whole set of classes dealing with taxonomy building from plain text
		//should be moved in an own small project: functionality is currently shared
		//between matrix-generation, matrix-review, etc-site (for oto2)
		LinkedList<RankData> rankDatas = new LinkedList<RankData>();
		Map<Rank, RankData> rankDataMap = new HashMap<Rank, RankData>();
		for(Element taxonIdentification : taxonIdentifications) {
			String authority = taxonIdentification.getAttributeValue("authority"); 
			String date = taxonIdentification.getAttributeValue("date");
			Rank rank = Rank.valueOf(taxonIdentification.getAttributeValue("rank").toUpperCase());
			String name = taxonIdentification.getValue();
			RankData rankData = new RankData(rank, name, authority, date);
			rankDatas.add(rankData);
			rankDataMap.put(rank, rankData);
			//taxonNameBuilder.append(taxonName.getAttributeValue("rank") + "=" + taxonName.getValue() + ",");
		}
		Collections.sort(rankDatas);
		if(rankDatas.contains(Rank.GENUS)) {
			String fullName = "";
			boolean found = false;
			for(int i=0; i<rankDatas.size(); i++) {
				RankData rankData = rankDatas.get(i);
				if(rankData.getRank().equals(Rank.GENUS)) {
					found = true;
				}
				if(found)
					fullName += rankData.getName() + " ";
			}
			result = fullName.substring(0, fullName.length() - 1);
		} else {
			result = rankDatas.getLast().getName();
		}
		
		Element author = source.getChild("author");
		Element date = source.getChild("date");
		Element title = source.getChild("title");
		Element pages = source.getChild("pages");
		String sourceString = "";
		if(author != null)
			sourceString += author.getText();
		if(date != null)
			sourceString += ", " + date.getText();
		if(title != null)
			sourceString += ", " + title.getText();
		if(pages != null)
			sourceString += ", " + pages.getText();
		
		String rankAuthor = rankDatas.getLast().getAuthor();
		if(rankAuthor == null || rankAuthor.isEmpty())
			rankAuthor = "unknown";
		String rankDate = rankDatas.getLast().getDate();
		if(rankDate == null || rankDate.isEmpty())
			rankDate = "unknown";
		
		result += " sec. " + rankAuthor + ", " + rankDate + " (from "+ sourceString + ")";
		return result;
	}
	
	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.ONTOLOGIZE)) {
						return task;
			}
		}
		return null;
	}
	
	@Override
	public List<Task> getResumables(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getResumableTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.ONTOLOGIZE)) {
				result.add(task);
			}
		}
		return result;
	}
	
	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws OntologizeException {
		final OntologizeConfiguration config = getOntologizeConfiguration(task);
		
		//remove ontologize configuration
		if(config.getInput() != null)
			try {
				fileService.setInUse(authenticationToken, false, config.getInput(), task);
			} catch (PermissionDeniedException e) {
				throw new OntologizeException(task);
			}
		daoManager.getOntologizeConfigurationDAO().remove(config);
		
		//remove task
		daoManager.getTaskDAO().removeTask(task);
		if(task.getConfiguration() != null)
			//remove configuration
			daoManager.getConfigurationDAO().remove(task.getConfiguration().getConfiguration());
	
		//cancel possible futures: not for this task type
	}
	
	private OntologizeConfiguration getOntologizeConfiguration(Task task) throws OntologizeException {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof OntologizeConfiguration))
			throw new OntologizeException(task);
		final OntologizeConfiguration ontologizeConfiguration = (OntologizeConfiguration)configuration;
		return ontologizeConfiguration;
	}

	@Override
	public Task goToTaskStage(AuthenticationToken token, Task task,	TaskStageEnum taskStageEnum) {
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.ONTOLOGIZE);
		TaskStage taskStage = daoManager.getTaskStageDAO().getOntologizeTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}


	@Override
	public boolean isValidOntology(AuthenticationToken authenticationToken, String filePath) {
		boolean readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission)
			return false;
		boolean isDirectory = true;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return false;
		}
		if(!isDirectory)
			return false;
		List<String> files;
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return false;
		}
		if(files.isEmpty() || files.size() > 1)
			return false;
		
		boolean statementFound = false;
		for(String file : files) {
			//validate ontology format 
		}
		return true;
	}
	
	@Override
	public boolean isValidInput(AuthenticationToken authenticationToken, String filePath) {
		boolean readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission)
			return false;
		boolean isDirectory = true;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return false;
		}
		if(!isDirectory)
			return false;
		List<String> files;
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			return false;
		}
		if(files.isEmpty())
			return false;
		
		//extra validation, since a valid taxon description is automatically also a valid marked up taxon description according to 
		//the schema. Check for min. 1 statement
		boolean statementFound = false;
		for(String file : files) {
			boolean valid;
			try {
				valid = fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, filePath + File.separator + file);
			} catch (PermissionDeniedException | GetFileContentFailedException e) {
				log(LogLevel.ERROR, "validation of "+file+ " threw exceptions");
				return false;
			}

			if(!valid){
				log(LogLevel.ERROR, file+ " is not valid");
				return false;
			}
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document;
			try {
				document = saxBuilder.build(new File(filePath + File.separator + file));
			} catch (JDOMException | IOException e) {
				log(LogLevel.ERROR, "SAXBuilder cannot build "+(filePath + File.separator + file)+ ".");
				return false;
			}
			XPathFactory xPathFactory = XPathFactory.instance();
			XPathExpression<Element> xPathExpression = 
					xPathFactory.compile("/bio:treatment/description[@type=\"morphology\"]/statement", Filters.element(), 
							null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics")); 
			if(!xPathExpression.evaluate(document).isEmpty()) {
				statementFound = true;
			}
			if(!statementFound){
				log(LogLevel.ERROR, "no statement found in file "+file+ ".");
			}
		}
		return statementFound;	
	}
	
	private void createOntologyFile(AuthenticationToken token, Task task) throws Exception {
		final OntologizeConfiguration config = this.getOntologizeConfiguration(task);
		collectionService.getOWL(config.getOntologizeCollectionId(), config.getOntologizeCollectionSecret());
		/*Collection collection = new Collection();
		collection.setId(config.getOntologizeCollectionId());
		collection.setSecret(config.getOntologizeCollectionSecret());
		collection.setTaxonGroup(edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(config.getTaxonGroup().getName()));
		toOntologyService.storeLocalOntologiesToFile(collection);*/
	}
	
	@Override
	public String downloadOntologize(AuthenticationToken token, Task task) throws Exception {
		createOntologyFile(token, task);
		final OntologizeConfiguration config = this.getOntologizeConfiguration(task);
		
		String zipSource = Configuration.compressedFileBase + File.separator + token.getUserId() + File.separator + "ontologize" + 
				File.separator + task.getId() + File.separator + task.getName() + "_ontologies";
		
		File zipSourceFile = new File(zipSource);
		try {
			FileUtils.deleteDirectory(zipSourceFile);
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't clean/remove directory to zip", e);
			throw new OntologizeException(task);
		}
		zipSourceFile.mkdirs();
		
		
		String ontologyPath = edu.arizona.biosemantics.oto2.ontologize2.server.Configuration.collectionsDirectory 
				+ File.separator + config.getOntologizeCollectionId() + File.separator + "owl";	
		File ontologyFile = new File(ontologyPath);
		for(File file : ontologyFile.listFiles())
			try {
				FileUtils.copyFileToDirectory(file,
						zipSourceFile);
			} catch (IOException e) {
				log(LogLevel.ERROR, "Couldn't copy ontology file", e);
				throw new OntologizeException(task);
			}
		
		String zipFilePath = Configuration.compressedFileBase + File.separator + token.getUserId() + File.separator + 
				"ontologize" + File.separator + task.getId() + File.separator + task.getName() + "_ontologies.zip";
		JavaZipper zipper = new JavaZipper();
		try {
			zipFilePath = zipper.zip(zipSource, zipFilePath);
		} catch (Exception e) {
			throw new OntologizeException("Download failed");
		}
		if(zipFilePath != null)
			return zipFilePath;
		throw new OntologizeException("Download failed");
	}
	
	
	@Override
	public Collection build(AuthenticationToken token, Task task) throws OntologizeException {
		OntologizeConfiguration config = getOntologizeConfiguration(task);
		int collectionId = config.getOntologizeCollectionId();
		String collectionSecret = config.getOntologizeCollectionSecret();
		try {
			Collection collection = collectionService.get(collectionId, collectionSecret);
			return collection;
		} catch (Exception e) {
			throw new OntologizeException(task);
		}
	}

	@Override
	public Task output(AuthenticationToken token, Task task) throws Exception {
		this.createOntologyFile(token, task);
		
		OntologizeConfiguration config = getOntologizeConfiguration(task);
		config.setOutput(config.getInput() + "_output_by_OB_task_" + task.getName());
			
		String outputDirectory = config.getOutput();			
		String outputDirectoryParentResult;
		try {
			outputDirectoryParentResult = fileService.getParent(token, outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException(task);
		}
		String outputDirectoryNameResult;
		try {
			outputDirectoryNameResult = fileService.getFileName(token, outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException(task);
		}
			
		//find a suitable destination filePath
		String createDirectoryResult;
		try {
			createDirectoryResult = fileService.createDirectory(new AdminAuthenticationToken(), outputDirectoryParentResult, 
				outputDirectoryNameResult, true);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new OntologizeException(task);
		}
		
		int ontologizeCollectionId = config.getOntologizeCollectionId();
		String ontologyTempFilePath = edu.arizona.biosemantics.oto2.ontologize2.server.Configuration.collectionsDirectory 
			+ File.separator + ontologizeCollectionId + File.separator + "owl";
		try {
			File ontologyTempFile = new File(ontologyTempFilePath);
			for(File file : ontologyTempFile.listFiles())
				FileUtils.copyFileToDirectory(file, new File(createDirectoryResult));
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't copy ontology to file manager", e);
			throw new OntologizeException();
		}
		
		//update task
		config.setOutput(createDirectoryResult);
		task.setResumable(true);
		//task.setComplete(true);
		//task.setCompleted(new Date());
		daoManager.getTaskDAO().updateTask(task);
		
		daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectoryResult);
		
		return task;
	}

	@Override
	public Collection addInput(AuthenticationToken token, Task task, String input) throws OntologizeException {
		OntologizeConfiguration config = this.getOntologizeConfiguration(task);
		try {
			Collection collection = collectionService.get(config.getOntologizeCollectionId(), config.getOntologizeCollectionSecret());
			List<Candidate> newTerms = getCandidates(input);
			collection.add(newTerms);
			collectionService.update(collection);
			contextService.insert(collection.getId(), collection.getSecret(), getContexts(token, input, collection));
			return collection;
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't add input to collection", e);
			throw new OntologizeException();
		}
	}

}
