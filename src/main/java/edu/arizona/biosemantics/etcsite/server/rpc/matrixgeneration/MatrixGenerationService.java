package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.rpc.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.shared.db.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.ConfigurationDAO;
import edu.arizona.biosemantics.etcsite.shared.db.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.MatrixGenerationConfigurationDAO;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.db.TaskDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.db.TaskStageDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TaskType;
import edu.arizona.biosemantics.etcsite.shared.db.TaskTypeDAO;
import edu.arizona.biosemantics.etcsite.shared.db.TasksOutputFilesDAO;
import edu.arizona.biosemantics.etcsite.shared.db.UserDAO;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum;
import edu.arizona.biosemantics.matrixgeneration.taxonomy.TaxonRank;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;

public class MatrixGenerationService extends RemoteServiceServlet implements IMatrixGenerationService  {

	private static class RankData {
		
		private Level rank;
		private String value;
		private String authority;
		private String date;
		
		private static HashMap<RankData, RankData> instances = new HashMap<RankData, RankData>();
		
		public static RankData getInstanceFor(Level rank, String value, String authority, String date) {
			RankData rankData = new RankData(rank, value, authority, date);
			if(!instances.containsKey(rankData))
				instances.put(rankData, rankData);
			return instances.get(rankData);
		}
		
		private RankData(Level rank, String value, String authority, String date) {
			super();
			this.rank = rank;
			this.value = value;
			this.authority = authority;
			this.date = date;
		}
		public Level getRank() {
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
	private IFilePermissionService filePermissionService = new FilePermissionService();
	private int maximumThreads = 10;
	private ListeningExecutorService executorService;
	private Map<Integer, ListenableFuture<Boolean>> activeProcessFutures = new HashMap<Integer, ListenableFuture<Boolean>>();

	
	public MatrixGenerationService() {
		executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
	}
	
	@Override
	public RPCResult<Task> start(AuthenticationToken authenticationToken, String taskName, String input) {	
		try {
			RPCResult<Boolean> sharedResult = filePermissionService.isSharedFilePath(authenticationToken.getUsername(), input);
			if(!sharedResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't verify permission on input directory");
			RPCResult<String> fileNameResult = fileService.getFileName(authenticationToken, input);
			if(!fileNameResult.isSucceeded())
				return new RPCResult<Task>(false, "Couldn't find file name for import");
			if(sharedResult.getData()) {
				RPCResult<String> destinationResult = 
						fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUsername(), 
								fileNameResult.getData(), true);
				RPCResult<Void> destination = fileService.copyFiles(authenticationToken, input, destinationResult.getData());
				if(!destinationResult.isSucceeded() || !destination.isSucceeded())
					return new RPCResult<Task>(false, "Couldn't copy shared files to an owned destination for input to task");
				input = destinationResult.getData();
			}
			
			MatrixGenerationConfiguration matrixGenerationConfiguration = new MatrixGenerationConfiguration();
			matrixGenerationConfiguration.setInput(input);	
			matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_" + taskName);
			matrixGenerationConfiguration = MatrixGenerationConfigurationDAO.getInstance().addMatrixGenerationConfiguration(matrixGenerationConfiguration);
			
			edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION;
			TaskType dbTaskType = TaskTypeDAO.getInstance().getTaskType(taskType);
			TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.INPUT.toString());
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			Task task = new Task();
			task.setName(taskName);
			task.setResumable(true);
			task.setUser(user);
			task.setTaskStage(taskStage);
			task.setTaskConfiguration(matrixGenerationConfiguration);
			task.setTaskType(dbTaskType);
						
			task = TaskDAO.getInstance().addTask(task);
			taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
			task.setTaskStage(taskStage);
			TaskDAO.getInstance().updateTask(task);

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
				final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
				TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.PROCESS.toString());
				task.setTaskStage(taskStage);
				task.setResumable(false);
				TaskDAO.getInstance().updateTask(task);
				
				String input = matrixGenerationConfiguration.getInput();
				RPCResult<String> createDirResult = fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, 
						String.valueOf(task.getId()), false);
				if(!createDirResult.isSucceeded()) 
					return new RPCResult<Task>(false, createDirResult.getMessage());
				fileService.createDirectory(new AdminAuthenticationToken(), Configuration.matrixGeneration_tempFileBase, String.valueOf(task.getId()), false);
				String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
				
				MatrixGeneration matrixGeneration = new MatrixGeneration(input, outputFile);
				final ListenableFuture<Boolean> futureResult = executorService.submit(matrixGeneration);
				this.activeProcessFutures.put(configuration.getConfiguration().getId(), futureResult);
				futureResult.addListener(new Runnable() {
				     	public void run() {
				     		try {
				     			activeProcessFutures.remove(configuration.getConfiguration().getId());
				     			if(!futureResult.isCancelled()) {
				     				Boolean result = futureResult.get();
									TaskStage newTaskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.REVIEW.toString());
									task.setTaskStage(newTaskStage);
									task.setResumable(true);
									TaskDAO.getInstance().updateTask(task);
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
	
	@Override
	public RPCResult<TaxonMatrix> review(AuthenticationToken authenticationToken, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<TaxonMatrix>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			
			String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
			TaxonMatrix matrix = readFile(outputFile);
			return new RPCResult<TaxonMatrix>(true, matrix);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<TaxonMatrix>(false, "Internal Server Error");
		}
	}
	
	@Override
	public RPCResult<Task> completeReview(AuthenticationToken authenticationToken, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Task>(false, "Not a compatible task");
			
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			final TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(TaskStageEnum.OUTPUT.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			TaskDAO.getInstance().updateTask(task);
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
		

	}
	
	private TaxonMatrix createSampleMatrix() {
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
			Taxon t1 = new Taxon("server" + i * 4 + 1, Level.FAMILY, "rosacea", "author1", "1979", "this is the description about t1");
			Taxon t2 = new Taxon("server" +  i * 4 + 2, Level.GENUS, "rosa", "author2", "1985",  "this is the description about t2");
			Taxon t3 = new Taxon("server" +  i * 4 + 3, Level.SPECIES,
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
			Taxon t4 = new Taxon("server" +  i * 4 + 4, Level.VARIETY,
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
		
		return taxonMatrix;
	}

	private TaxonMatrix readFile(String filePath) throws Exception {
		//return createSampleMatrix();

		CSVReader reader = new CSVReader(new FileReader(filePath), '\t', CSVWriter.NO_QUOTE_CHARACTER);
		
		List<Character> characters = new LinkedList<Character>();
		String[] head = reader.readNext();
		for(int i=1; i<head.length; i++) {
			String character = head[i];
			characters.add(new Character(character));
		}
		TaxonMatrix taxonMatrix = new TaxonMatrix(characters);
		
	    List<TaxonName> taxonNames = new LinkedList<TaxonName>();
	    Map<RankData, Taxon> rankTaxaMap = new HashMap<RankData, Taxon>();
	    List<String[]> allLines = reader.readAll();
	    //init all taxa
	    for(int i=0; i<allLines.size(); i++) {
	    	String[] line = allLines.get(i);
	    	String name = line[0];
	    	TaxonName taxonName = createTaxonName(name);
	    	taxonNames.add(taxonName);
	    	Taxon taxon = createPlainTaxon(String.valueOf(i), taxonName);
	    	rankTaxaMap.put(taxonName.getRankData().getLast(), taxon);
	    }
	    
	    //assign parents or root
	    for(TaxonName taxonName : taxonNames) {
	    	LinkedList<RankData> rankData = taxonName.getRankData();
	    	Taxon taxon = rankTaxaMap.get(rankData.getLast());
	    	if(rankData.size() == 1) 
	    		taxonMatrix.addRootTaxon(taxon);
		    if(rankData.size() > 1) {
		    	RankData parentRankData = rankData.get(rankData.size() - 2);
		    	Taxon parent = rankTaxaMap.get(parentRankData);
		    	taxonMatrix.addTaxon(parent, taxon);
		    }
	    }
	    
	    //set values
	    for(int i=0; i<allLines.size(); i++) {
	    	String[] line = allLines.get(i);
		    for(int j=1; j<line.length; j++) {
	    		taxonMatrix.setValue(rankTaxaMap.get(taxonNames.get(i).getRankData().getLast()), characters.get(j-1), new Value(line[j]));
	    	}
	    }
	    
	    reader.close();
	    return taxonMatrix; 
	}



	private TaxonName createTaxonName(String name) {
		String[] ranksAuthorParts = name.split(":");
    	String[] authorParts = ranksAuthorParts[1].split(",");
    	String author = authorParts[0];
    	String date = authorParts[1];
    	
    	String[] ranks = ranksAuthorParts[0].split(";");
    	LinkedList<RankData> rankData = new LinkedList<RankData>();
    	
    	for(String rank : ranks) {
    		String[] rankParts = rank.split(",");
    		String authority = "";
    		String rankDate = "";
    		String value = "";
    		Level level = Level.UNRANKED;
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
        			level = Level.valueOf(rankPartValues[0].toUpperCase());
        			value = rankPartValues[1];
        		} catch(Exception e) {
        		}
        	}
    		
    		rankData.add(RankData.getInstanceFor(level, value, authority, rankDate));
    	}
    			
		TaxonName result = new TaxonName(rankData, author.split("=")[1], date.split("=")[1]);
		return result;
	}

	//TODO: Integrate rank authority and date
	private Taxon createPlainTaxon(String id, TaxonName taxonName) {
		RankData lowestRank = taxonName.getRankData().getLast();
		return new Taxon("server" + id, lowestRank.getRank(), lowestRank.getValue(), taxonName.getAuthor(), taxonName.getDate(), "");
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
			
			//copy the output files to the directory
			String matrixGenerationOutputDirectory = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId();		
			RPCResult<Void> copyResult = fileService.copyFiles(new AdminAuthenticationToken(), matrixGenerationOutputDirectory, createDirectoryResult.getData());
			if(!copyResult.isSucceeded()) {
				return new RPCResult<Task>(false, copyResult.getMessage());
			}
			
			//update task
			matrixGenerationConfiguration.setOutput(createDirectoryResult.getData());
			task.setResumable(false);
			task.setComplete(true);
			task.setCompleted(new Date());
			TaskDAO.getInstance().updateTask(task);
			
			TasksOutputFilesDAO.getInstance().addOutput(task, createDirectoryResult.getData());
			
			return new RPCResult<Task>(true, task);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Task>(false, "Internal Server Error");
		}
	}

	@Override
	public RPCResult<Task> getLatestResumable(AuthenticationToken authenticationToken) {
		try {
			ShortUser user = UserDAO.getInstance().getShortUser(authenticationToken.getUsername());
			List<Task> tasks = TaskDAO.getInstance().getOwnedTasks(user.getId());
			for(Task task : tasks) {
				if(task.isResumable() && 
						task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION)) {
							//SemanticMarkupConfiguration configuration = SemanticMarkupConfigurationDAO.getInstance().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
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
				MatrixGenerationConfigurationDAO.getInstance().remove(matrixGenerationConfiguration);
			}
			
			//remove task
			if(task != null) {
				TaskDAO.getInstance().removeTask(task);
				if(task.getConfiguration() != null)
					
					//remove configuration
					ConfigurationDAO.getInstance().remove(task.getConfiguration().getConfiguration());
			
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
		super.destroy();
	}

	@Override
	public RPCResult<Void> save(AuthenticationToken authenticationToken, TaxonMatrix matrix, Task task) {
		try {
			final AbstractTaskConfiguration configuration = task.getConfiguration();
			if(!(configuration instanceof MatrixGenerationConfiguration))
				return new RPCResult<Void>(false, "Not a compatible task");
			final MatrixGenerationConfiguration matrixGenerationConfiguration = (MatrixGenerationConfiguration)configuration;
			
			String outputFile = Configuration.matrixGeneration_tempFileBase + File.separator + task.getId() + File.separator + "Matrix.mx";
			saveFile(matrix, outputFile);
			return new RPCResult<Void>(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new RPCResult<Void>(false, "Internal Server Error");
		}
	}

	//TODO: Add RankData authority and date
	private void saveFile(TaxonMatrix matrix, String outputFile) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(outputFile), '\t', CSVWriter.NO_QUOTE_CHARACTER);
		
		String[] line = new String[matrix.getCharacterCount() + 1];
		line[0] = "Name";
		
		for(int i=1; i<=matrix.getCharacterCount(); i++) {
			line[i] = matrix.getCharacter(i - 1).toString();
		}
		writer.writeNext(line);
		
		for(int i=0; i<matrix.getTaxaCount(); i++) {
			Taxon taxon = matrix.getTaxon(i);
			line = new String[matrix.getCharacterCount() + 1];
			line[0] = getTaxonName(taxon);
			
			for(int j=1; j<matrix.getCharacterCount(); j++) {
				Character character = matrix.getCharacter(j - 1);
				line[j] = taxon.get(character).toString();
			}
			writer.writeNext(line);
		}
		writer.flush();
		writer.close();
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
			name += current.getLevel() + "=" + current.getName() + ";";
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
			TaskType taskType = TaskTypeDAO.getInstance().getTaskType(edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum.MATRIX_GENERATION);
			TaskStage taskStage = TaskStageDAO.getInstance().getMatrixGenerationTaskStage(taskStageEnum.toString());
			task.setTaskStage(taskStage);
			task.setResumable(true);
			task.setComplete(false);
			task.setCompleted(null);
			TaskDAO.getInstance().updateTask(task);
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
		for(String file : files) {
			RPCResult<Boolean> validResult = fileFormatService.isValidMarkedupTaxonDescription(authenticationToken, filePath + File.separator + file);
			if(!validResult.isSucceeded())
				return new RPCResult<Boolean>(false, validResult.getMessage());
			if(!validResult.getData())
				return new RPCResult<Boolean>(true, false);
		}
		return new RPCResult<Boolean>(true, true);
	}

}
