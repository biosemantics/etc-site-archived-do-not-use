package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.TaskTypeDAO;
import edu.arizona.biosemantics.etcsite.server.rpc.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationService;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon.Rank;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static class RankData {
		
		private RankData parent;
		private Rank rank;
		private String value;
		private String authority;
		private String date;
		
		public RankData(RankData parent, Rank rank, String value, String authority, String date) {
			super();
			this.parent = parent;
			this.rank = rank;
			this.value = value;
			this.authority = authority;
			this.date = date;
		}
		public RankData getParent() {
			return parent;
		}
		public Rank getRank() {
			return rank;
		}
		public String getValue() {
			return value;
		}
		public String getAuthority() {
			return authority;
		}
		public String getDate() {
			return date;
		}		
		public String toString() {
			return rank + "=" + value;// + "," + authority + "," + date;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((authority == null) ? 0 : authority.hashCode());
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result
					+ ((parent == null) ? 0 : parent.hashCode());
			result = prime * result + ((rank == null) ? 0 : rank.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RankData other = (RankData) obj;
			if (authority == null) {
				if (other.authority != null)
					return false;
			} else if (!authority.equals(other.authority))
				return false;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;
			if (rank != other.rank)
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
	}
	
	private class TaxonName {
		private LinkedList<RankData> rankData;
		private String author;
		private String date;		
		public TaxonName(LinkedList<RankData> rankData, String author, String date) {
			super();
			this.rankData = rankData;
			this.author = author;
			this.date = date;
		}
		public LinkedList<RankData> getRankData() {
			return rankData;
		}
		public String getAuthor() {
			return author;
		}
		public String getDate() {
			return date;
		}
		public String toString() {
			return rankData.toString() + ":" + author + "," + date;
		}
	}
	
	private static final long serialVersionUID = -7871896158610489838L;
	private IFileService fileService = new FileService();
	private IFileFormatService fileFormatService = new FileFormatService();
	private IFileAccessService fileAccessService = new FileAccessService();
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Boolean>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Boolean>>();
	private Map<Integer, MatrixGeneration> activeMatrixGenerations = new HashMap<Integer, MatrixGeneration>();
	private DAOManager daoManager = new DAOManager();
	private Emailer emailer = new Emailer();
	
	public MatrixGenerationService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Configuration.maxActiveMatrixGeneration));
	}
	
	@Override
	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String input) {	
		try {
			RPCResult<Boolean> sharedResult = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), input);
			if(!sharedResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't verify permission on input directory");
			RPCResult<String> fileNameResult = fileService.getFileName(authenticationToken, input);
			if(!fileNameResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't find file name for import");
			if(sharedResult.getData()) {
				RPCResult<String> destinationResult = 
						fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
								fileNameResult.getData(), true);
				RPCResult<Void> destination = fileService.copyFiles(authenticationToken, input, destinationResult.getData());
				if(!destinationResult.isSucceeded() || !destination.isSucceeded())
					return new RPCResult<Task>(false, "Couldn't copy shared files to an owned destination for input to task");
				input = destinationResult.getData();
			}
			
			MatrixGenerationConfiguration matrixGenerationConfiguration = new MatrixGenerationConfiguration();
			matrixGenerationConfiguration.setInput(input);	
			matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + taskName);
			matrixGenerationConfiguration = daoManager.getMatrixGenerationConfigurationDAO().addMatrixGenerationConfiguration(matrixGenerationConfiguration);
			
			edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION;
			TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
			TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.INPUT.toString());
			ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			task.setTaskConfiguration(matrixGenerationConfiguration);
			task.setTaskType(dbTaskType);
			
			task = daoManager.getTaskDAO().addTask(task);
			taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
			task.setTaskStage(taskStage);
			daoManager.getTaskDAO().updateTask(task);

			fileService.setInUse(authenticationToken, true, input, task);
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> process(AuthenticationToken authenticationToken, final Task task) {	
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
				
			//browser back button may invoke another "learn"
			if(activeProcessFutures.containsKey(configuration.getConfiguration().getId())) {
				return new RPCResult<Task>(true, task);
			} else {
				final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				daoManager.getTaskDAO().updateTask(task);
				
				String input = matrixGenerationConfiguration.getInput();
				RPCResult<String> createDirResult = fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, 
						String.valueOf(task.getId()), false);
				
				if(!createDirResult.isSucceeded()) 
					return new RPCResult<Task>(false, createDirResult.getMessage());
				fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, String.valueOf(task.getId()), false);
				final String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
				
				MatrixGeneration matrixGeneration = new ExtraJvmMatrixGeneration(input, outputFile);
				activeMatrixGenerations.put(configuration.getConfiguration().getId(), matrixGeneration);
				final ListenableFuture<Boolean> futureResult = executorService.submit(matrixGeneration);
				this.activeProcessFutures.put(configuration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			TaxonMatrix matrix = createTaxonMatrix(matrixGenerationConfiguration, outputFile);
				     			Model model = new Model(matrix);
				     			serializeMatrix(model, Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser");
				     			
				     			activeMatrixGenerations.remove(configuration.getConfiguration().getId());
				     			activeProcessFutures.remove(configuration.getConfiguration().getId());
				     			if(!futureResult.isCancelled()) {
				     				Boolean result = futureResult.get();
									TaskStage newTaskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.REVIEW.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									daoManager.getTaskDAO().updateTask(task);
									
									// send an email to the user who owns the task.
									sendFinishedGeneratingMatrixEmail(task);
				     			}
				     		} catch (Exception e) {
								e.printStackTrace();
							}
				     	}
				     }, executorService);
				
				return new RPCResult<Task>(true, task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	private void serializeMatrix(Model model, String file) throws IOException {
		try(ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			output.writeObject(model);
		}
	}
	
	private Model unserializeMatrix(String file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			Model model = (Model)input.readObject();
			return model;
		}
	}
	
	@Override
	public RPCResult<Model> review(AuthenticationToken authenticationToken, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Model>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			
			Model model = unserializeMatrix(Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser");
			return new RPCResult<Model>(true, model);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Model>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> completeReview(AuthenticationToken authenticationToken, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(TaskStageEnum.OUTPUT.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			daoManager.getTaskDAO().updateTask(task);
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
		

	}
	
	private void sendFinishedGeneratingMatrixEmail(Task task){
		try {
			String email = daoManager.getUserDAO().getUser(task.getUser().getId()).getEmail();
			String subject = Configuration.finishedMatrixgenerationGenerateSubject.replace("<taskname>", task.getName());
			String body = Configuration.finishedMatrixgenerationGenerateBody.replace("<taskname>", task.getName());
			
			emailer.sendEmail(email, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
	}
	
	/*private TaxonMatrix createSampleMatrix() {
		List<Character> characters = new LinkedList<Character>();
		Organ o1 = new Organ("stem");
		Organ o2 = new Organ("leaf");
		Organ o3 = new Organ("head");
		Character a1 = new Character("length", o1);
		Character a2 = new Character("shape", o1);
		Character a3 = new Character("architecture", o1);
		
		Character b1 = new Character("width", o2);
		Character b2 = new Character("shape", o2);
		Character b3 = new Character("pubescence", o2);
		
		Character c1 = new Character("size", o3);
		Character c2 = new Character("color", o3);
		Character c3 = new Character("architecture", o3);
		
		
		characters.add(a1);
		characters.add(a2);
		characters.add(a3);
		characters.add(b1);
		characters.add(b2);
		characters.add(b3);
		characters.add(c1);
		characters.add(c2);
		characters.add(c3);
		
		for(int i=0; i<20; i++) {
			Character o = new Character("o" + i, o2);
			characters.add(o);
		}
		
		TaxonMatrix taxonMatrix = new TaxonMatrix(characters);

		for(int i=0; i<1; i++) {
			Taxon t1 = new Taxon("server" + i * 4 + 1, Rank.FAMILY, "rosacea", "author1", "1979", "this is the description about t1");
			Taxon t2 = new Taxon("server" +  i * 4 + 2, Rank.GENUS, "rosa", "author2", "1985",  "this is the description about t2");
			Taxon t3 = new Taxon("server" +  i * 4 + 3, Rank.SPECIES,
					"example", "author3", "2002", 
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
							+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
							+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
							+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
							+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
							+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
							+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
							+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
							+ "tincidunt diam nec urna. Curabitur velit.");
			Taxon t4 = new Taxon("server" +  i * 4 + 4, Rank.VARIETY,
					"prototype", "author4", "2014", 
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
							+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
							+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
							+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
							+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
							+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
							+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
							+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
							+ "tincidunt diam nec urna. Curabitur velit.");
			
			taxonMatrix.addRootTaxon(t1);
			taxonMatrix.addTaxon(t1, t2);
			taxonMatrix.addTaxon(t2, t3);
			taxonMatrix.addTaxon(t2, t4);

			Random random = new Random();
			taxonMatrix.setValue(t1, b1, new Value(String.valueOf(random.nextInt(50))));
		}
		
		/*for(int i=5; i<50; i++) {
			Taxon t5 = new Taxon("server" + i, Level.SPECIES, "t123", "a", "2", "de");
			taxonMatrix.addRootTaxon(t4);
		}*/
		
	/*	return taxonMatrix;
	}*/
	
	public static void main(String[] args) throws Exception {
		MatrixGenerationService service = new MatrixGenerationService();
		service.createTaxonMatrix("C:/test/Test_mmm", "C:/test/Test_mmm.mx");
	}

	private TaxonMatrix createTaxonMatrix(String inputDirectory, String filePath) throws Exception {
		
		try(CSVReader reader = new CSVReader(new FileReader(filePath), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER)) {
			List<Character> charactesInFile = new LinkedList<Character>();
			String[] head = reader.readNext();
			
			HashMap<String, Organ> organMap = new HashMap<String, Organ>();
			List<Organ> hierarhicalCharacters = new LinkedList<Organ>();
			for(int i=2; i<head.length; i++) {
				String character = head[i];
				int ofIndex = character.lastIndexOf(" of ");
				
				String organ = "";
				if(character.contains(" of ")) {
					organ = character.substring(ofIndex + 4);
				} 
				
				Organ o;
				if(!organMap.containsKey(organ)) {
					o = new Organ(organ);
					organMap.put(organ, o);
					hierarhicalCharacters.add(o);
				} else {
					o = organMap.get(organ);
				}

				character = character.substring(0, ofIndex);
				Character c = new Character(character, o, o.getFlatCharacters().size());
				charactesInFile.add(c);
			}			
						
		    List<TaxonName> taxonNames = new LinkedList<TaxonName>();
		    Map<RankData, Taxon> rankTaxaMap = new HashMap<RankData, Taxon>();
		    Map<RankData, RankData> rankDataInstances = new HashMap<RankData, RankData>();
		    List<String[]> allLines = reader.readAll();
		    //init all taxa
		    for(int i=0; i<allLines.size(); i++) {
		    	String[] line = allLines.get(i);
		    	String sourceFile = line[1];
		    	String name = line[0];
		    	TaxonName taxonName = createTaxonName(name, rankDataInstances);
		    	taxonNames.add(taxonName);
		    	
		    	String description = getMorphologyDescription(new File(inputDirectory, sourceFile));
		    	Taxon taxon = createPlainTaxon(taxonName, description);
		    	rankTaxaMap.put(taxonName.getRankData().getLast(), taxon);
		    }
		    
		    //assign parents or root
		    List<Taxon> hierarchyTaxa = new LinkedList<Taxon>();
		    for(TaxonName taxonName : taxonNames) {
		    	LinkedList<RankData> rankData = taxonName.getRankData();
		    	Taxon taxon = rankTaxaMap.get(rankData.getLast());
		    	if(rankData.size() == 1) 
		    		hierarchyTaxa.add(taxon);
			    if(rankData.size() > 1) {
			    	int parentRankIndex = rankData.size() - 2;
			    	Taxon parentTaxon = null;
			    	while(parentTaxon == null && parentRankIndex >= 0) {
				    	RankData parentRankData = rankData.get(parentRankIndex);
			    		parentTaxon = rankTaxaMap.get(parentRankData);
			    		parentRankIndex--;
			    	}
			    	if(parentTaxon == null)
			    		hierarchyTaxa.add(taxon);
			    	else
			    		parentTaxon.addChild(taxon);
			    }
		    }
		    TaxonMatrix taxonMatrix = new TaxonMatrix(hierarhicalCharacters, hierarchyTaxa);
		    
		    //set values
		    for(int i=0; i<allLines.size(); i++) {
		    	String[] line = allLines.get(i);
			    for(int j=2; j<line.length; j++) {
			    	Taxon taxon = rankTaxaMap.get(taxonNames.get(i).getRankData().getLast());
			    	Character character = charactesInFile.get(j-2);
		    		taxonMatrix.setValue(taxon, character, new Value(line[j]));
		    	}
		    }
		    
		    return taxonMatrix; 
		}
	}
	
	private TaxonMatrix createTaxonMatrix(MatrixGenerationConfiguration matrixGenerationConfiguration, String filePath) throws Exception {
		String inputDirectory = matrixGenerationConfiguration.getInput();
		return createTaxonMatrix(inputDirectory, filePath);
	}

	private String getMorphologyDescription(File file) throws JDOMException, IOException {
		StringBuilder result = new StringBuilder();
		SAXBuilder sax = new SAXBuilder();
		Document doc = sax.build(file);
		
		XPathFactory xpfac = XPathFactory.instance();
		XPathExpression<Element> xp = xpfac.compile("//bio:treatment/description[@type='morphology']", Filters.element(), null,
				Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
		for(Element element : xp.evaluate(doc)) {
			List<Element> statements = element.getChildren("statement");
			for(Element statement : statements) {
				Element text = statement.getChild("text");
				result.append(text.getText() + " ");
			}
			result.append("\n\n");
		}
		return result.toString();
	}

	private TaxonName createTaxonName(String name, Map<RankData, RankData> rankDataInstances) {
		String[] ranksAuthorParts = name.split(":");
    	String[] authorParts = ranksAuthorParts[1].split(",");
    	String author = authorParts[0];
    	String date = authorParts[1];
    	
    	String[] ranks = ranksAuthorParts[0].split(";");
    	LinkedList<RankData> rankData = new LinkedList<RankData>();
    	
    	RankData parent = null;
    	for(String rank : ranks) {
    		String[] rankParts = rank.split(",");
    		String authority = "";
    		String rankDate = "";
    		String value = "";
    		Rank level = Rank.UNRANKED;
    		for(String rankPart : rankParts) {
        		String[] rankPartValues = rankPart.split("=");
        		if(rankPartValues[0].equals("authority")) {
        			authority = rankPartValues[1];
        			continue;
        		}
        		if(rankPartValues[0].equals("date")) {
        			rankDate = rankPartValues[1];
        			continue;
        		}
        		try {
        			level = Rank.valueOf(rankPartValues[0].toUpperCase());
        			value = rankPartValues[1];
        		} catch(Exception e) {
        		}
        	}
    		
    		RankData rankDataInstance = new RankData(parent, level, value, authority, date);
			if(!rankDataInstances.containsKey(rankDataInstance))
				rankDataInstances.put(rankDataInstance, rankDataInstance);
			rankDataInstance = rankDataInstances.get(rankDataInstance);
    		rankData.add(rankDataInstance);
    		parent = rankDataInstance;
    	}
    			
		TaxonName result = new TaxonName(rankData, author.split("=")[1], date.split("=")[1]);
		return result;
	}

	//TODO: Integrate rank authority and date
	private Taxon createPlainTaxon(TaxonName taxonName, String description) {
		RankData lowestRank = taxonName.getRankData().getLast();
		return new Taxon(lowestRank.getRank(), lowestRank.getValue(), taxonName.getAuthor(), taxonName.getDate(), description);
	}
	
	@Override
	public RPCResult<Task> output(AuthenticationToken authenticationToken, Task task) {
		try {
			AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + task.getName());
			
			String outputDirectory = matrixGenerationConfiguration.getOutput();			
			RPCResult<String> outputDirectoryParentResult = fileService.getParent(authenticationToken, outputDirectory);
			RPCResult<String> outputDirectoryNameResult = fileService.getFileName(authenticationToken, outputDirectory);
			if(!outputDirectoryParentResult.isSucceeded() || !outputDirectoryNameResult.isSucceeded())
				return new RPCResult<Task>(false, outputDirectoryParentResult.getMessage());
			
			//find a suitable destination filePath
			RPCResult<String> createDirectoryResult = fileService.createDirectory(authenticationToken, outputDirectoryParentResult.getData(), 
					outputDirectoryNameResult.getData(), true);
			
			if(!createDirectoryResult.isSucceeded()) 
				return new RPCResult<Task>(false, createDirectoryResult.getMessage());
			
			String csvContent = getCSVMatrix(unserializeMatrix(Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser").getTaxonMatrix());
			RPCResult<String> createFileResult = 
					fileService.createFile(new AdminAuthenticationToken(), createDirectoryResult.getData(), "Matrix.mx", true);
			if(!createFileResult.isSucceeded())
				return new RPCResult<Task>(false, createFileResult.getMessage());
			RPCResult<Void> setFileContentResult = fileAccessService.setFileContent(authenticationToken, 
					createDirectoryResult.getData() + File.separator + "Matrix.mx", csvContent);
			if(!setFileContentResult.isSucceeded())
				return new RPCResult<Task>(false, setFileContentResult.getMessage());
			
			//update task
			matrixGenerationConfiguration.setOutput(createDirectoryResult.getData());
			task.setResumable(false);
			task.setComplete(true);
			task.setCompleted(new Date());
			daoManager.getTaskDAO().updateTask(task);
			
			daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectoryResult.getData());
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	private String getCSVMatrix(TaxonMatrix matrix) throws IOException {
		//TODO: Add RankData authority and date
		try(StringWriter stringWriter = new StringWriter()) {
			try(CSVWriter writer = new CSVWriter(stringWriter, '\t', CSVWriter.NO_QUOTE_CHARACTER)) {
			
				String[] line = new String[matrix.getCharacterCount() + 1];
				line[0] = "Name";
				
				for(int i=1; i<=matrix.getCharacterCount(); i++) {
					line[i] = matrix.getFlatCharacters().get(i - 1).toString();
				}
				writer.writeNext(line);
				
				for(Taxon taxon : matrix.getHierarchyTaxaDFS()) {
					line = new String[matrix.getCharacterCount() + 1];
					line[0] = getTaxonName(taxon);
					
					for(int j=1; j<matrix.getCharacterCount(); j++) {
						Character character = matrix.getFlatCharacters().get(j - 1);
						line[j] = matrix.getValue(taxon, character).toString();
					}
					writer.writeNext(line);
				}
				writer.flush();
				String result = stringWriter.toString();
				return result;
			}
		}
	}

	@Override
	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken) {
		try {
			ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
			List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
			for(Task task : tasks) {
				if(task.isResumable() && 
						task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION)) {
							//SemanticMarkupConfiguration configuration = SemanticMarkupdaoManager.getConfigurationDAO().getInstance().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
							return new RPCResult<Task>(true, task);
				}
			}
			return new RPCResult<Task>(true, "", null);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Void> cancel(AuthenticationToken authenticationToken, Task task) {
		try {
			AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
						
			//remove matrix generation configuration
			if(matrixGenerationConfiguration != null) {
				if(matrixGenerationConfiguration.getInput() != null)
					fileService.setInUse(authenticationToken, false, matrixGenerationConfiguration.getInput(), task);
				daoManager.getMatrixGenerationConfigurationDAO().remove(matrixGenerationConfiguration);
			}
			
			//remove task
			if(task != null) {
				daoManager.getTaskDAO().removeTask(task);
				if(task.getConfiguration() != null)
					
					//remove configuration
					daoManager.getConfigurationDAO().remove(task.getConfiguration().getConfiguration());
			
				//cancel possible futures
				if(task.getTaskStage() != null) {
					switch(TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
					case INPUT:
						break;
					case PROCESS:
						if(activeProcessFutures.containsKey(matrixGenerationConfiguration.getConfiguration().getId())) {
							ListenableFuture<Boolean> processFuture = this.activeProcessFutures.get(matrixGenerationConfiguration.getConfiguration().getId());
							processFuture.cancel(true);
						}
						if(activeMatrixGenerations.containsKey(matrixGenerationConfiguration.getConfiguration().getId())) {
							activeMatrixGenerations.get(matrixGenerationConfiguration.getConfiguration().getId()).destroy();
						}
						break;
					case OUTPUT:
						break;
					default:
						break;
					}
				}
			}
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	@Override
	public void destroy() {
		this.executorService.shutdownNow();
		for(MatrixGeneration matrixGeneration : activeMatrixGenerations.values()) {
			matrixGeneration.destroy();
		}
		super.destroy();
	}

	@Override
	public RPCResult<Void> save(AuthenticationToken authenticationToken, Model model, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			
			String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
			serializeMatrix(model, Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "TaxonMatrix.ser");
			//saveFile(matrix, outputFile);
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	private String getTaxonName(Taxon taxon) {
		String name = "";
		ArrayList<Taxon> taxonPath = new ArrayList<Taxon>();
		Taxon current = taxon;
		taxonPath.add(taxon);
		while(current.hasParent()) {
			current = current.getParent();
			taxonPath.add(current);
		}
		
		ListIterator<Taxon> it = taxonPath.listIterator(taxonPath.size());
		while(it.hasPrevious()) {
			current = it.previous();
			name += current.getRank() + "=" + current.getName() + ";";
		}
		name = name.substring(0, name.length() - 1);
		name += ":";
		name += "author=" + taxon.getAuthor() + ",";
		name += "date=" + taxon.getYear();
	
		return name;
	}

	@Override
	public RPCResult<Task> goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {
		try {
			TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = daoManager.getTaskStageDAO().getMatrixGenerationTaskStage(taskStageEnum.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			task.setComplete(false);
			task.setCompleted(null);
			daoManager.getTaskDAO().updateTask(task);
			return new RPCResult<Task>(true, task);
		} catch(Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Boolean> isValidInput(AuthenticationToken authenticationToken, String filePath) {
		RPCResult<Boolean> readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission.isSucceeded())
			return new RPCResult<Boolean>(false, readPermission.getMessage());
		if(!readPermission.getData()) 
			return new RPCResult<Boolean>(true, false);
		RPCResult<Boolean> directoryResult = fileService.isDirectory(authenticationToken, filePath);
		if(!directoryResult.isSucceeded())
			return new RPCResult<Boolean>(false, directoryResult.getMessage());
		if(!directoryResult.getData())
			return new RPCResult<Boolean>(true, false);
		RPCResult<List<String>> filesResult = fileService.getDirectoriesFiles(authenticationToken, filePath);
		if(!filesResult.isSucceeded())
			return new RPCResult<Boolean>(false, filesResult.getMessage());
		List<String> files = filesResult.getData();
		
		//extra validation, since a valid taxon description is automatically also a valid marked up taxon description according to 
		//the schema. Check for min. 1 statement
		boolean statementFound = false;
		for(String file : files) {
			RPCResult<Boolean> validResult = fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, filePath + File.separator + file);

			if(!validResult.isSucceeded())
				return new RPCResult<Boolean>(false, validResult.getMessage());
			if(!validResult.getData())
				return new RPCResult<Boolean>(true, false);
			
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document;
			try {
				document = saxBuilder.build(new File(filePath + File.separator + file));
				XPathFactory xPathFactory = XPathFactory.instance();
				XPathExpression<Element> xPathExpression = 
						xPathFactory.compile("/bio:treatment/description[@type=\"morphology\"]/statement", Filters.element(), 
								null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics")); 
				if(!xPathExpression.evaluate(document).isEmpty()) {
					statementFound = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new RPCResult<Boolean>(false, "Couldn't read XML file");
			}
		}
		return new RPCResult<Boolean>(true, statementFound);
	}

	@Override
	public RPCResult<String> saveMatrix(AuthenticationToken authenticationToken, 
			Task task, Model model) {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof MatrixGenerationConfiguration))
			return null;
		final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
		
		try {
			String path = Configuration.tempFiles + 
					File.separator + "matrix-review" + File.separator + authenticationToken.getUserId() +
					File.separator + "matrix_task-" + task.getName() + ".csv";
			File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			String[] characters = new String[model.getTaxonMatrix().getCharacterCount() + 1];
			List<Character> flatCharacters = model.getTaxonMatrix().getFlatCharacters();
			int columns = flatCharacters.size() + 1;
			characters[0] = "Taxa/Characters";
			for(int i=0; i<flatCharacters.size(); i++) {
				characters[i+1] = flatCharacters.get(i).toString();
			}
			try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
				csvWriter.writeNext(characters);
				for(Taxon taxon : model.getTaxonMatrix().getFlatTaxa()) {
					String[] line = new String[columns];
					line[0] = taxon.getFullName();
					for(int i=0; i<flatCharacters.size(); i++) {
						Character character = flatCharacters.get(i);
						line[i+1] = model.getTaxonMatrix().getValue(taxon, character).toString();
					}
					csvWriter.writeNext(line);
				}
			}
			
			/*ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			String jsonModel = writer.writeValueAsString(model);
			try (FileWriter fileWriter = new FileWriter(new File(path))) {
				fileWriter.write(jsonModel);
			}*/
			return new RPCResult<String>(true, "", path);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<String>(false, "Internal Server Error" ,"");
		}
	}

}
