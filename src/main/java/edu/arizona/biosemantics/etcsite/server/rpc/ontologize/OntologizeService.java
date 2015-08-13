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
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.JavaZipper;
import edu.arizona.biosemantics.etcsite.server.Zipper;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
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
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Context;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateOntologyException;

public class OntologizeService extends RemoteServiceServlet implements IOntologizeService {
	
	private IFileService fileService;
	private IFileFormatService fileFormatService;
	private IFilePermissionService filePermissionService;
	private IFileAccessService fileAccessService;
	private Emailer emailer;
	private edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService collectionService;
	private edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService toOntologyService;
	private edu.arizona.biosemantics.oto2.ontologize.shared.rpc.IContextService contextService;
	private DAOManager daoManager;

	@Inject
	public OntologizeService(FileService fileService, FileFormatService fileFormatService, FilePermissionService filePermissionService, 
			FileAccessService fileAccessService, 
			edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService collectionService,
			edu.arizona.biosemantics.oto2.ontologize.server.rpc.ContextService contextService,
			DAOManager daoManager, Emailer emailer) {
		this.fileService = fileService;
		this.fileFormatService = fileFormatService;
		this.filePermissionService = filePermissionService;
		this.fileAccessService = fileAccessService;
		this.collectionService = collectionService;
		this.contextService = contextService;
		this.daoManager = daoManager;
		this.emailer = emailer;
	}
	
	@Override
	public Task startWithOntologyCreation(AuthenticationToken token, String taskName, String input, String taxonGroup, 
			String ontologyPrefix) throws OntologizeException {
		Collection collection = createOntologizeCollection(token, taskName, input, 
				edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup));
		String tempOntology = getOntologyFromCreation(ontologyPrefix, 
				edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup), collection, true);
		return doStart(token, taskName, input, taxonGroup, tempOntology, collection);
	}
	
	@Override
	public Task startWithOntologySelection(AuthenticationToken token, String taskName, String input, String taxonGroup, 
			String ontology) throws OntologizeException {	
		Collection collection = createOntologizeCollection(token, taskName, input, 
				edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup));
		String tempOntology = getOntologyFromSelection(token, ontology, edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(taxonGroup), 
				collection);
		Task task = doStart(token, taskName, input, taxonGroup, tempOntology, collection);
		
		try {
			fileService.setInUse(token, true, ontology, task);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException(task);
		}
		
		return task;
	}
	
	private Collection createOntologizeCollection(AuthenticationToken token, String name, String input, 
			edu.arizona.biosemantics.common.biology.TaxonGroup taxonGroup) throws OntologizeException {
		Collection collection = new Collection();
		collection.setName(name);
		collection.setTaxonGroup(taxonGroup);
		collection.setTerms(getTerms(input));
		
		try {
			collection = collectionService.insert(collection);
			contextService.insert(collection.getId(), collection.getSecret(), getContexts(token, input, collection));
		} catch (Exception e) {
			throw new OntologizeException();
		}
		return collection;
	}
	
	private List<Context> getContexts(AuthenticationToken token, String input, Collection collection) throws OntologizeException {
		List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Context> contexts = 
				new LinkedList<edu.arizona.biosemantics.oto2.ontologize.shared.model.Context>();
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
					contexts.add(new edu.arizona.biosemantics.oto2.ontologize.shared.model.Context(collection.getId(), 
							getTaxonIdentification(token, input + File.separator + file), 
							description.getContent()));
				} catch (PermissionDeniedException | GetFileContentFailedException e) {
					throw new OntologizeException();
				}
			}
		}
		return contexts;
	}

	private List<Term> getTerms(String input) throws OntologizeException {
		MarkupResultReader reader = new MarkupResultReader();
		File inputFile = new File(input);
		List<MarkupResultReader.BiologicalEntity> structures;
		List<MarkupResultReader.Character> characters;
		List<MarkupResultReader.Character> rangeValueCharacters;
		try {
			structures = reader.getBiologicalEntities(inputFile);
			characters = reader.getCharacters(inputFile, false);
			rangeValueCharacters = reader.getRangeValueCharacters(inputFile, false);
		} catch (JDOMException | IOException e) {
			log(LogLevel.ERROR, "Couldn't parse input", e);
			throw new OntologizeException(e);
		}
		
		List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Term> terms = 
				new LinkedList<edu.arizona.biosemantics.oto2.ontologize.shared.model.Term>();					
		Set<String> containedStructures = new HashSet<String>();
		for(BiologicalEntity structure : structures) {
			if(!containedStructures.contains(structure.getName() + structure.getIri())) {
				terms.add(new edu.arizona.biosemantics.oto2.ontologize.shared.model.Term(
						structure.getName(), structure.getIri(), "/structure", "structure"));
				containedStructures.add(structure.getName() + structure.getIri());
			}
		}
		Set<String> containedCharacters = new HashSet<String>();
		for(MarkupResultReader.Character character : characters) {
			//filter comparison values such as "wider than long". "twice of leaf"
			if(character.getValue().split("\\W+").length < 3) {
				if(!containedCharacters.contains(character.getValue() + character.getCategory() + character.getIri())) {
					terms.add(new edu.arizona.biosemantics.oto2.ontologize.shared.model.Term(
							character.getValue(), character.getIri(), "/character/" + character.getCategory(), character.getCategory()));
					containedCharacters.add(character.getValue() + character.getCategory() + character.getIri());
				}
			}
		}
		return terms;
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
			XPathExpression<Element> taxonNameXPath = xpfac.compile("/bio:treatment/taxon_identification[@status='ACCEPTED']/taxon_name", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));

			if(doc != null) {
				List<Element> sources = sourceXPath.evaluate(doc);
				List<Element> taxonIdentification = taxonNameXPath.evaluate(doc);
				return createTaxonIdentification(sources.get(0), taxonIdentification);
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
			String author = ""; //not used for oto2
			String data = ""; //not used for oto2
			Rank rank = Rank.valueOf(taxonIdentification.getAttributeValue("rank").toUpperCase());
			String name = taxonIdentification.getValue();
			RankData rankData = new RankData(rank, name, "", "");
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
		
		result += " sec. " + sourceString;
		return result;
	}

	private String getOntologyFromSelection(AuthenticationToken token, String ontology, edu.arizona.biosemantics.common.biology.TaxonGroup taxonGroup, 
			Collection collection) throws OntologizeException {
		boolean isShared = filePermissionService.isSharedFilePath(token.getUserId(), ontology);
		String ontologyFileName;
		try {
			ontologyFileName = fileService.getFileName(token, ontology);
		} catch (PermissionDeniedException e) {
			throw new OntologizeException();
		}
		if(isShared) {
			String destination;
			try {
				destination = fileService.createDirectory(token, Configuration.fileBase + File.separator + token.getUserId(), 
						ontologyFileName, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new OntologizeException();
			}
			try {
				fileService.copyFiles(token, ontology, destination);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new OntologizeException();
			}
			ontology = destination;
		}
		
		String ontologyPath;
		try {
			String owlFile = getOntologyFile(token, ontology);
			if(owlFile == null)
				throw new OntologizeException("Could not find ontology file");
			ontologyPath = ontology + File.separator + owlFile;
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Couldn't get ontology file from directory", e);
			throw new OntologizeException();
		}
		
		String ontologyAcronym = getOntologyAcronym(ontologyPath);
		String ontologyTempFile = this.getOntologyTempFile(collection, ontologyAcronym);
		File ontologyFile = new File(ontology);
		for(File file : ontologyFile.listFiles())
			try {
				FileUtils.copyDirectoryToDirectory(file,
						new File(ontologyTempFile));
			} catch (IOException e) {
				log(LogLevel.ERROR, "Couldn't copy ontology to ontologize cache", e);
				throw new OntologizeException();
			}
		ontologyTempFile = getOntologyFromCreation(ontologyAcronym, taxonGroup, collection, false);		
		return ontologyTempFile;
	}

	private String getOntologyFile(AuthenticationToken token, String filePath) throws PermissionDeniedException {
		List<String> files = fileService.getDirectoriesFiles(token, filePath);
		for(String file : files) {
			if(!file.startsWith("module."))
				return file;
		}
		return null;
	}

	private String getOntologyAcronym(String ontology) {
		// can i extract it from file reliably? is it really necessary to have?
		return FilenameUtils.removeExtension(new File(ontology).getName());
	}

	private Task doStart(AuthenticationToken token, String taskName, String input, String taxonGroup, 
			String tempOntology, Collection collection) throws OntologizeException {
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

	private String getOntologyFromCreation(String ontologyAcronym, 
			edu.arizona.biosemantics.common.biology.TaxonGroup taxonGroup, Collection collection, boolean createFile) throws OntologizeException {
		Ontology ontology = new Ontology();
		ontology.setAcronym(ontologyAcronym);
		ontology.setName(ontologyAcronym);
		ontology.setBioportalOntology(false);
		Set<edu.arizona.biosemantics.common.biology.TaxonGroup> taxonGroups = new HashSet<edu.arizona.biosemantics.common.biology.TaxonGroup>();
		taxonGroups.add(taxonGroup);
		ontology.setTaxonGroups(taxonGroups);
	
		try {
			ontology = toOntologyService.createOntology(collection, ontology, createFile);
		} catch (CreateOntologyException e) {
			throw new OntologizeException();
		}
		
		return getOntologyTempFile(collection, ontology.getAcronym());
	}
		

	private String getOntologyTempFile(Collection collection, String ontologyAcronym) {
		return edu.arizona.biosemantics.oto2.ontologize.server.Configuration.collectionOntologyDirectory 
			+ File.separator + collection.getId() + File.separator + ontologyAcronym
			+ File.separator + ontologyAcronym + ".owl";
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
	
	
	
	@Override
	public String downloadOntologize(AuthenticationToken token, Task task) throws OntologizeException {
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
		
		Collection collection = new Collection();
		collection.setId(config.getOntologizeCollectionId());
		collection.setSecret(config.getOntologizeCollectionSecret());
		collection.setTaxonGroup(edu.arizona.biosemantics.common.biology.TaxonGroup.valueFromDisplayName(config.getTaxonGroup().getName()));
		List<Ontology> ontologies;
		try {
			ontologies = toOntologyService.getLocalOntologies(collection);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get ontologies of collection", e);
			throw new OntologizeException(task);
		}
		for (Ontology ontology : ontologies) {
			String ontologyPath = edu.arizona.biosemantics.oto2.ontologize.server.Configuration.collectionOntologyDirectory
					+ File.separator
					+ collection.getId()
					+ File.separator
					+ ontology.getAcronym();
			
			File ontologyFile = new File(ontologyPath);
			for(File file : ontologyFile.listFiles())
				try {
					FileUtils.copyFileToDirectory(file,
							zipSourceFile);
				} catch (IOException e) {
					log(LogLevel.ERROR, "Couldn't copy ontology file", e);
					throw new OntologizeException(task);
				}
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
	public Task output(AuthenticationToken token, Task task) throws OntologizeException {
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
			createDirectoryResult = fileService.createDirectory(token, outputDirectoryParentResult, 
				outputDirectoryNameResult, true);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new OntologizeException(task);
		}
		
		int ontologizeCollectionId = config.getOntologizeCollectionId();
		String ontologyAcronym = getOntologyAcronym(config.getOntologyFile());
		String ontologyTempFilePath = edu.arizona.biosemantics.oto2.ontologize.server.Configuration.collectionOntologyDirectory 
			+ File.separator + ontologizeCollectionId + File.separator + ontologyAcronym;
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
		task.setResumable(false);
		task.setComplete(true);
		task.setCompleted(new Date());
		daoManager.getTaskDAO().updateTask(task);
		
		daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectoryResult);
		
		return task;
	}

	@Override
	public void addInput(AuthenticationToken token, Task task, String input) throws OntologizeException {
		OntologizeConfiguration config = this.getOntologizeConfiguration(task);
		try {
			Collection collection = collectionService.get(config.getOntologizeCollectionId(), config.getOntologizeCollectionSecret());
			List<Term> newTerms = getTerms(input);
			List<Term> existingTerms = collection.getTerms();
			
			Map<String, Term> termsMap = new HashMap<String, Term>();
			for(Term term : existingTerms) 
				termsMap.put(term.getCategory() + " " + term.getTerm(), term);
			List<Term> toAddTerms = new LinkedList<Term>();
			for(Term term : newTerms)
				if(!termsMap.containsKey(term.getCategory() + " " + term.getTerm()))
					toAddTerms.add(term);
			
			collection.addTerms(toAddTerms);
			collectionService.update(collection);
			contextService.insert(collection.getId(), collection.getSecret(), getContexts(token, input, collection));
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't add input to collection", e);
			throw new OntologizeException();
		}
	}

}
