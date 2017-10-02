package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.context.shared.Context;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.JavaZipper;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.SemanticMarkupDBDAO;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.model.AbstractTaskConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.TaskType;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.model.process.semanticmarkup.BracketValidator;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.LearnInvocation;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.PreprocessedDescription;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.FileDeleteFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.SetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.UserNotFoundException;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;

public class SemanticMarkupService extends RemoteServiceServlet implements ISemanticMarkupService  {

	private static final long serialVersionUID = -7871896158610489838L;
	private ICollectionService collectionService;
	private IFileAccessService fileAccessService;
	private IFileService fileService;
	private IFileFormatService fileFormatService;
	private IFilePermissionService filePermissionService;
	private IUserService userService;
	private BracketValidator bracketValidator = new BracketValidator();
	private ListeningScheduledExecutorService executorService;
	private Map<Integer, ListenableFuture<LearnResult>> activeLearnFutures = new HashMap<Integer, ListenableFuture<LearnResult>>();
	private Map<Integer, ListenableFuture<ParseResult>> activeParseFutures = new HashMap<Integer, ListenableFuture<ParseResult>>();
	private Map<Integer, Learn> activeLearns = new HashMap<Integer, Learn>();
	private Map<Integer, Parse> activeParses = new HashMap<Integer, Parse>();
	private DAOManager daoManager;
	private Emailer emailer;
	private ICollectionService otoCollectionService;
	private ToOTOSender2 otoSender;
	
	@Inject
	public SemanticMarkupService(ICollectionService collectionService, IFileAccessService fileAccessService, IFileService fileService, 
			IFileFormatService fileFormatService, IFilePermissionService filePermissionService, IUserService userService, 
			DAOManager daoManager, Emailer emailer, 
			edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService otoCollectionService, 
			ToOTOSender2 otoSender) {
		this.collectionService = collectionService;
		this.fileAccessService = fileAccessService; 
		this.fileService = fileService;
		this.fileFormatService = fileFormatService;
		this.filePermissionService = filePermissionService;
		this.userService = userService;
		this.daoManager = daoManager;
		this.emailer = emailer;
		this.otoCollectionService = otoCollectionService;
		this.otoSender = otoSender;
		executorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(Configuration.maxActiveSemanticMarkup));
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	
	
	@Override
	public Task start(AuthenticationToken authenticationToken, String taskName, String filePath, String taxonGroup, boolean useEmptyGlossary) throws SemanticMarkupException {
		boolean isShared = filePermissionService.isSharedFilePath(authenticationToken.getUserId(), filePath);
		String fileNameResult;
		try {
			fileNameResult = fileService.getFileName(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		if(isShared) {
			String destinationResult;
			try {
				destinationResult = fileService.createDirectory(authenticationToken, Configuration.fileBase + File.separator + authenticationToken.getUserId(), 
						fileNameResult, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new SemanticMarkupException();
			}
			try {
				fileService.copyFiles(authenticationToken, filePath, destinationResult);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new SemanticMarkupException();
			}
			filePath = destinationResult;
		}
		
		List<String> directoriesFilesResult;
		try {
			directoriesFilesResult = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		int numberOfInputFiles = directoriesFilesResult.size();
		TaxonGroup group = daoManager.getTaxonGroupDAO().getTaxonGroup(taxonGroup);
		SemanticMarkupConfiguration config = new SemanticMarkupConfiguration();
		config.setInput(filePath);	
		config.setTaxonGroup(group);
		config.setUseEmptyGlossary(useEmptyGlossary);
		config.setNumberOfInputFiles(numberOfInputFiles);
		config.setOutput(config.getInput() + "_output_by_TC_task_" + taskName);
		config = daoManager.getSemanticMarkupConfigurationDAO().addSemanticMarkupConfiguration(config);
		
		edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum taskType = edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP;
		TaskType dbTaskType = daoManager.getTaskTypeDAO().getTaskType(taskType);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.INPUT.toString());
		TinyUser user = daoManager.getUserDAO().getTinyUser(authenticationToken.getUserId());
		Task task = new Task();
		task.setName(taskName);
		task.setResumable(true);
		task.setUser(user);
		task.setTaskStage(taskStage);
		task.setTaskConfiguration(config);
		task.setTaskType(dbTaskType);

		task = daoManager.getTaskDAO().addTask(task);
		taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
		List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
		if(result.isEmpty())
			if(taxonGroup.equals("Bacteria"))
				taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PARSE_TEXT.toString());
			else
				taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		
		try {
			fileService.setInUse(authenticationToken, true, filePath, task);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		return task;
	}

	@Override
	public List<PreprocessedDescription> preprocess(AuthenticationToken authenticationToken, Task task) {	
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PREPROCESS_TEXT.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		//do preprocessing here, return result immediately or always only return an invocation
		//and make user come back when ready?
		
		List<PreprocessedDescription> result = this.getPreprocessedDescriptions(authenticationToken, task);
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		final Learn learn = new InJvmLearn(new DAOManager(), null
				, "Plant", false, 
				"/home/biosemantics/workspace2015/etc-site/users/1/CoolFiles", "test" + ('a' + (int)(Math.random()*26)) + ('a' + (int)(Math.random()*26)), "", "");
		learn.call();
	}
	
	@Override
	public LearnInvocation learn(final AuthenticationToken authenticationToken, final Task task) throws SemanticMarkupException {
		String numberOfSentences = getNumberOfSentences();
		String numberOfWords = getNumberOfWords();
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);

		//browser back button may invoke another "learn"
		if(activeLearnFutures.containsKey(config.getConfiguration().getId())) {
			return new LearnInvocation(numberOfSentences, numberOfWords);
		} else {
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.LEARN_TERMS.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			daoManager.getTaskDAO().updateTask(task);
			
			String taxonGroup = config.getTaxonGroup().getName();
			boolean useEmptyGlossary = config.isUseEmptyGlossary();
			final String input = config.getInput();
			String tablePrefix = String.valueOf(task.getId());
			String source = input; //maybe something else later
			String operator;
			try {
				operator = userService.getUser(authenticationToken).getFullNameEmailAffiliation();
			} catch (UserNotFoundException e1) {
				throw new SemanticMarkupException(task);
			}
			String bioportalUserId = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalUserId();
			String bioportalAPIKey = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalAPIKey();
			final Learn learn = new ExtraJvmLearn(daoManager, taxonGroup, useEmptyGlossary, input, tablePrefix, source, operator);
			//final Learn learn = new InJvmLearn(daoManager, fileService, taxonGroup, useEmptyGlossary, input, tablePrefix, source, operator);
			//debug locally, use InJVMLearn
			
			
			activeLearns.put(config.getConfiguration().getId(), learn);
			final ListenableFuture<LearnResult> futureResult = executorService.submit(learn);
			try {
				futureResult.get(Configuration.semanticMarkup_learnStep_maxRunningTimeMinutes, TimeUnit.MINUTES);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} catch (ExecutionException e2) {
				e2.printStackTrace();
			} catch (TimeoutException e2) {
				// Task took too long. 
				futureResult.cancel(true);
				learn.destroy();
				log(LogLevel.ERROR,
						"Semantic markup took too long and was canceled. (Learn step)");
			}
			
			activeLearnFutures.put(config.getConfiguration().getId(), futureResult);
			futureResult.addListener(new Runnable() {
			     	public void run() {
			     		try {
				     		Learn learn = activeLearns.remove(config.getConfiguration().getId());
				 			ListenableFuture<LearnResult> futureResult = activeLearnFutures.remove(config.getConfiguration().getId());
				 			
				     		if(learn.isExecutedSuccessfully()) {
				     			if(!futureResult.isCancelled()) {
				     				LearnResult result = null;
									try {
										result = futureResult.get();
									} catch (InterruptedException | ExecutionException e) {
										log(LogLevel.ERROR, "Couldn't get the produced learn result", e);
									}
									if(result != null) {
						     			config.setOtoUploadId(result.getOtoUploadId());
						     			config.setOtoSecret(result.getOtoSecret());
						     			try {
											createOTOContexts(authenticationToken, task, result, input);
										} catch (SemanticMarkupException e1) { }
						     			try {
											otoCollectionService.initializeFromHistory(new Collection(result.getOtoUploadId(), result.getOtoSecret()));
											Collection collection = otoCollectionService.get(result.getOtoUploadId(), result.getOtoSecret());
											otoCollectionService.update(collection, true);
										} catch (Exception e) {
											log(LogLevel.ERROR, "Couldn't initialize the uploaded oto dataset from history", e);
										}
						     			daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(config);
										TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
										task.setTaskStage(newTaskStage);
										task.setResumable(true);
										daoManager.getTaskDAO().updateTask(task);
										
										// send an email to the user who owns the task.
										sendFinishedLearningTermsEmail(task);
									}
				     			}
				     		} else {
				     			learn.destroy();
				     			task.setFailed(true);
								task.setFailedTime(new Date());
								task.setTooLong(futureResult.isCancelled());
								daoManager.getTaskDAO().updateTask(task);
				     		}
			     		} catch(Throwable t) {
			     			learn.destroy();
				     		task.setFailed(true);
							task.setFailedTime(new Date());
							daoManager.getTaskDAO().updateTask(task);
			     		}
			     	}
			     }, executorService);
			
			return new LearnInvocation(numberOfSentences, numberOfWords);
		}
	}
	
	@Override
	public Task review(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.REVIEW_TERMS.toString());
		task.setTaskStage(taskStage);
		daoManager.getTaskDAO().updateTask(task);
		task = daoManager.getTaskDAO().getTask(task.getId());
		return task;
	}
	
	@Override
	public void parse(final AuthenticationToken authenticationToken, final Task task) throws SemanticMarkupException {		
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		
		if(activeParseFutures.containsKey(config.getConfiguration().getId())) {
			return;
		} else {
			final TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
			TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.PARSE_TEXT.toString());
			task.setTaskStage(taskStage);
			task.setResumable(false);
			daoManager.getTaskDAO().updateTask(task);
			
			String taxonGroup = config.getTaxonGroup().getName();
			boolean useEmptyGlossary = config.isUseEmptyGlossary();
			final String input = config.getInput();
			String tablePrefix = String.valueOf(task.getId());
			String source = input; //maybe something else later
			String operator;
			try {
				operator = userService.getUser(authenticationToken).getFullNameEmailAffiliation();
			} catch (UserNotFoundException e) {
				throw new SemanticMarkupException(task);
			}
			String bioportalUserId = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalUserId();
			String bioportalAPIKey = daoManager.getUserDAO().getUser(authenticationToken.getUserId()).getBioportalAPIKey();
			
			final String outputDirectory = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "out";
			new File(outputDirectory).mkdirs();
			//Parse parse = new ExtraJvmParse(taxonGroup, useEmptyGlossary, input, tablePrefix, source, operator);
			Parse parse = new InJvmParse(taxonGroup, useEmptyGlossary, input, tablePrefix, source, operator);
			if(config.getTaxonGroup().getName().equals("Bacteria")) 
				//parse = new InJvmMicropieParse(input, outputDirectory, Configuration.micropie_models);
				parse = new ExtraJvmMicropieParse(input, outputDirectory, Configuration.micropie_models);
			
			final Parse finalParse = parse;
			
			//final Parse parse = new InJvmParse(taxonGroup, useEmptyGlossary, input, tablePrefix, source, operator);
			activeParses.put(config.getConfiguration().getId(), parse);
			final ListenableFuture<ParseResult> futureResult = executorService.submit(parse);
			try {
				futureResult.get(Configuration.semanticMarkup_parseStep_maxRunningTimeMinutes, TimeUnit.MINUTES);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} catch (ExecutionException e2) {
				e2.printStackTrace();
			} catch (TimeoutException e2) {
				// Task took too long. 
				futureResult.cancel(true);
				parse.destroy();
				log(LogLevel.ERROR,
						"Semantic markup took too long and was canceled. (Parse step)");
			}
			
			activeParseFutures.put(config.getConfiguration().getId(), futureResult);
			futureResult.addListener(new Runnable() {
				@Override
				public void run() {
					try {
						Parse parse = activeParses.remove(config.getConfiguration().getId());
						boolean charaparserParse = parse instanceof CharaparserParse;
						ListenableFuture<ParseResult> futureResult = activeParseFutures.remove(config.getConfiguration().getId());
						if(parse.isExecutedSuccessfully()) {
							if(!futureResult.isCancelled()) {
								List<String> files;
								boolean validResult = true;
								try {
									
									files = fileService.getDirectoriesFiles(new AdminAuthenticationToken(), outputDirectory);
								} catch (PermissionDeniedException e) {
									throw new SemanticMarkupException();
								}
								validResult = validateCharaparserOutput(task, parse, files, input, outputDirectory);		
								if(validResult) {
									config.setOutput(config.getInput() + "_output_by_TC_task_" + task.getName());
									config.setOutputTermReview(config.getInput() + "_TermsReviewed_by_TC_task_" + task.getName());
									String createDirectory = getOutputDirectory(config.getOutput(), task);
									String createTermReviewDirectory = getOutputDirectory(config.getOutputTermReview(), task);
									copyCharaparserOutput(createDirectory, task);
									if(charaparserParse)
										saveOto(authenticationToken, createTermReviewDirectory, task);
									config.setOutput(createDirectory);
									config.setOutputTermReview(createTermReviewDirectory);
									daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(config);
									daoManager.getTasksOutputFilesDAO().addOutput(task, createDirectory);
									daoManager.getTasksOutputFilesDAO().addOutput(task, createTermReviewDirectory);
									
									task.setResumable(true);
									//TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.TO_ONTOLOGIES.toString());
								    TaskStage newTaskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(TaskStageEnum.OUTPUT.toString());
								    task.setTaskStage(newTaskStage);
								    daoManager.getTaskDAO().updateTask(task);
								    sendFinishedParsingEmail(task);
								}
							}
						} else {
							parse.destroy();
							task.setFailed(true);
							task.setFailedTime(new Date());
							task.setTooLong(futureResult.isCancelled());
							daoManager.getTaskDAO().updateTask(task);
						}
					} catch(Throwable t) {
						finalParse.destroy();
			     		task.setFailed(true);
						task.setFailedTime(new Date());
						daoManager.getTaskDAO().updateTask(task);
		     		}
				}
			}, executorService);
		}
	}

	protected boolean validateCharaparserOutput(Task task, Parse parse, List<String> files, String input, String outputDirectory) throws SemanticMarkupException {
		boolean validResult = true;
		for(String file : files) {
			if(file.endsWith(".xml")) {
				try {
					validResult = fileFormatService.isValidMarkedupTaxonDescription(new AdminAuthenticationToken(), outputDirectory + File.separator + file);
				} catch (PermissionDeniedException | GetFileContentFailedException e) {
					throw new SemanticMarkupException();
				}
				if(!validResult){
					parse.destroy();
					String inputpath = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "input" + ".zip";
					String outputpath = outputDirectory + ".zip";
					JavaZipper zipper = new JavaZipper();
					try {
						inputpath = zipper.zip(input, inputpath);
						outputpath = zipper.zip(outputDirectory, outputpath);
					} catch (Exception e) {
						throw new SemanticMarkupException("Saving failed");
					}
					sendFailedParsingEmail(task, inputpath, outputpath);	//rewrite
					task.setResumable(false);
					daoManager.getTaskDAO().updateTask(task);
					task.setFailed(true);
					task.setFailedTime(new Date());
					/*try {
						fileService.deleteFile(new AdminAuthenticationToken(), inputpath);
						fileService.deleteFile(new AdminAuthenticationToken(), outputpath);
					} catch (PermissionDeniedException | FileDeleteFailedException e) {
						throw new SemanticMarkupException(task);
					}*/
					throw new SemanticMarkupException();
				}
			}
		}
		return validResult;
	}

	@Override
	public Task output(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {	
		task.setResumable(false);
		task.setComplete(true);
		task.setCompleted(new Date());
		//SemanticMarkupConfiguration semanticMarkupConfig=(SemanticMarkupConfiguration)task.getConfiguration();
		//String outputTermReview= semanticMarkupConfig.getOutput().replaceAll("_output_", "_TermsReviewed_");
		//((SemanticMarkupConfiguration)task.getConfiguration()).setOutputTermReview(outputTermReview);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}

	private void copyCharaparserOutput(String createDirectory, Task task) throws SemanticMarkupException {
		//copy the output files to the directory
		String charaParserOutputDirectory = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "out";		
		try {
			fileService.deleteFile(new AdminAuthenticationToken(), charaParserOutputDirectory + File.separator + "config.txt");
		} catch (PermissionDeniedException | FileDeleteFailedException e) {
			throw new SemanticMarkupException(task);
		}
		try {
			fileService.copyFiles(new AdminAuthenticationToken(), charaParserOutputDirectory, createDirectory);
		} catch (CopyFilesFailedException | PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
	}

	private String getOutputDirectory(String outputDirectory, Task task) throws SemanticMarkupException {
		String outputDirectoryParent;
		try {
			outputDirectoryParent = fileService.getParent(new AdminAuthenticationToken(), outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		String outputDirectoryName;
		try {
			outputDirectoryName = fileService.getFileName(new AdminAuthenticationToken(), outputDirectory);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		
		//find a suitable destination filePath
		String createDirectory;
		try {
			createDirectory = fileService.createDirectory(new AdminAuthenticationToken(), outputDirectoryParent, 
					outputDirectoryName, true);
		} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
			throw new SemanticMarkupException(task);
		}
		return createDirectory;
	}

	@Override
	public String checkValidInput(AuthenticationToken authenticationToken, String filePath) throws SemanticMarkupException {
		boolean readPermission = filePermissionService.hasReadPermission(authenticationToken, filePath);
		if(!readPermission) 
			return "File access denied";
		boolean isDirectory;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		if(!isDirectory)
			return "Invalid input selection: Selection should be directory";
		List<String> files;
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
		if(files.isEmpty())
			return "Invalid input directory: No files found in the folder.";
		for(String file : files) {
			boolean validResult;
			try {
				validResult = fileFormatService.isValidTaxonDescription(authenticationToken, filePath + File.separator + file);
			} catch (PermissionDeniedException | GetFileContentFailedException e) {
				throw new SemanticMarkupException();
			}
			if(!validResult)
				return "Invalid input: Error in taxon description";
		}
		String result = fileService.validateTaxonNames(authenticationToken, filePath);
		if(!result.equals("success")){
			return result;
		}
		return "valid";
	}

	@Override
	public String saveOto(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {
		String zipSource = Configuration.compressedFileBase + File.separator + authenticationToken.getUserId() + File.separator + "oto2" + 
				File.separator + task.getId() + File.separator + task.getName() + "_term_review";
		saveOto(authenticationToken, zipSource, task);
		
		String zipFilePath = zipSource + ".zip";
		JavaZipper zipper = new JavaZipper();
		try {
			zipFilePath = zipper.zip(zipSource, zipFilePath);
		} catch (Exception e) {
			throw new SemanticMarkupException("Saving failed");
		}
		if(zipFilePath != null)
			return zipFilePath;
		throw new SemanticMarkupException("Saving failed");
	}
	
	private void saveOto(AuthenticationToken authenticationToken, String destination, Task task) throws SemanticMarkupException {
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		int uploadId = config.getOtoUploadId();
		String secret = config.getOtoSecret();
		
		Collection collection;
		try {
			collection = otoCollectionService.get(uploadId, secret);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get oto collection", e);
			throw new SemanticMarkupException(task);
		}
		
		File file = new File(destination);
		file.mkdirs();
		createCategorizationFile(task, collection, destination);
		createSynonymFile(task, collection, destination);
		createCategoriesFile(task, collection, destination);
	}

	@Override
	public void importOto(Task task, String termCategorization, String synonymy) throws SemanticMarkupException {
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		int uploadId = config.getOtoUploadId();
		String secret = config.getOtoSecret();
		
		Collection collection = otoCollectionService.get(uploadId, secret);
		Map<String, Term> termsInCollection = new HashMap<String, Term>();
		for(Term term : collection.getTerms()) {
			termsInCollection.put(term.getTerm(), term);
		}
		Map<String, Label> labelsInCollection = new HashMap<String, Label>();
		for(Label label : collection.getLabels()) {
			labelsInCollection.put(label.getName(), label);
		}
		
		Bucket othersBucket = null;
		for(Bucket bucket : collection.getBuckets()) {
			if(bucket.getName().equalsIgnoreCase("Others")) {
				othersBucket = bucket;
				break;
			}
		}
		
		createTermCategorization(task, termCategorization, collection, termsInCollection, labelsInCollection, othersBucket);
		createSynonymy(task, synonymy, collection, termsInCollection, labelsInCollection, othersBucket);
		
		otoCollectionService.update(collection, false);
	}
	
	private void createSynonymy(Task task, String synonymy, Collection collection, Map<String, Term> termsInCollection, Map<String, 
			Label> labelsInCollection, Bucket othersBucket) throws SemanticMarkupException {
		List<String[]> lines = new LinkedList<String[]>();
		try(CSVReader reader = new CSVReader(new StringReader(synonymy))) {
			lines = reader.readAll();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes reader", e);
			throw new SemanticMarkupException(task);
		}
		for(String[] line : lines) {
			String label = line[0].trim();
			String mainTerm = line[1].trim();
			String synonymTerm = line[2].trim();
			
			Term collectionSynonymTerm = null;
			if(!termsInCollection.containsKey(synonymTerm)) {
				collectionSynonymTerm = new Term(synonymTerm);
				collectionSynonymTerm = otoCollectionService.addTerm(collectionSynonymTerm, othersBucket.getId());
				termsInCollection.put(synonymTerm, collectionSynonymTerm);
			} else {
				collectionSynonymTerm = termsInCollection.get(synonymTerm);
			}
			
			Term collectionMainTerm = null;
			if(!termsInCollection.containsKey(mainTerm)) {
				collectionMainTerm = new Term(mainTerm);
				collectionMainTerm = otoCollectionService.addTerm(collectionMainTerm, othersBucket.getId());
				termsInCollection.put(mainTerm, collectionMainTerm);
			} else {
				collectionMainTerm = termsInCollection.get(mainTerm);
			}
			
			Label collectionLabel = null;
			if(!labelsInCollection.containsKey(label)){
				collectionLabel = new Label(collection.getId(), label, "");
				collection.addLabel(collectionLabel);
				labelsInCollection.put(label, collectionLabel);
			} else {
				collectionLabel = labelsInCollection.get(label);
			}
			
			if(!collectionLabel.isMainTerm(collectionMainTerm)) {
				collectionLabel.addMainTerm(collectionMainTerm);
			}
			collectionLabel.addSynonym(collectionMainTerm, collectionSynonymTerm, true);
		}
	}

	private void createTermCategorization(Task task, String termCategorization, Collection collection, 
			Map<String, Term> termsInCollection, Map<String, Label> labelsInCollection, Bucket othersBucket) throws SemanticMarkupException {
		List<String[]> lines = new LinkedList<String[]>();
		try(CSVReader reader = new CSVReader(new StringReader(termCategorization))) {
			lines = reader.readAll();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes reader", e);
			throw new SemanticMarkupException(task);
		}
		
		if(othersBucket != null) {
			for(String[] line : lines) {
				String label = line[0].trim();
				String term = line[1].trim();
				if(!label.isEmpty()) {
					Term collectionTerm = null;
					Label collectionLabel = null;
					if(!termsInCollection.containsKey(term)) {
						collectionTerm = new Term(term);
						otoCollectionService.addTerm(collectionTerm, othersBucket.getId());
						termsInCollection.put(term, collectionTerm);
					} else if(termsInCollection.containsKey(term)) {
						collectionTerm = termsInCollection.get(term);
						List<Label> termsLabels = collection.getLabels(collectionTerm);
						for(Label termLabel : termsLabels) 
							termLabel.uncategorizeTerm(collectionTerm);
					}
					if(!labelsInCollection.containsKey(label)){
						collectionLabel = new Label(collection.getId(), label, "");
						collection.addLabel(collectionLabel);
						labelsInCollection.put(label, collectionLabel);
					} else {
						collectionLabel = labelsInCollection.get(label);
					}
					collectionLabel.addMainTerm(collectionTerm);
				} else {
					Term collectionTerm = new Term(term);
					if(!termsInCollection.containsKey(term)) {
						collectionTerm = otoCollectionService.addTerm(collectionTerm, othersBucket.getId());
						termsInCollection.put(term, collectionTerm);
					} else {
						collectionTerm = termsInCollection.get(term);
					}
					for(Label existingInLabel : collection.getLabels(collectionTerm)) {
						existingInLabel.uncategorizeTerm(collectionTerm);
					}
				}
			}
			
			/*for(String[] line : lines) {
				String label = line[0].trim();
				String term = line[1].trim();
				
				if(termsInCollection.containsKey(term) && labelsInCollection.containsKey(label)) {
					Term collectionTerm = termsInCollection.get(term);
					Label collectionLabel = labelsInCollection.get(label);
					
					
				}
			}*/
		}
	}

	private void createCategoriesFile(Task task, Collection collection, String destination) throws SemanticMarkupException {
		String path = destination + File.separator + "category_definition-task-" + task.getName() + ".csv";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not create file", e);
			throw new SemanticMarkupException(task);
		}
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(new String[] {"category", "definition"});
			for(Label label : collection.getLabels()) {
				csvWriter.writeNext(new String[] {label.getName(), label.getDescription()});
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes writer", e);
			throw new SemanticMarkupException(task);
		}
	}

	private void createSynonymFile(Task task, Collection collection, String destination) throws SemanticMarkupException {
		String path = destination + File.separator + "category_mainterm_synonymterm-task-" + task.getName() + ".csv";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not create file", e);
			throw new SemanticMarkupException(task);
		}
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(new String[] {"category", "term", "synonym"});
			for(Label label : collection.getLabels()) {
				for(Term mainTerm : label.getMainTerms()) {
					List<Term> synonyms = label.getSynonyms(mainTerm);
					for(Term synonym : synonyms) {
						csvWriter.writeNext(new String[] { label.getName(), mainTerm.getTerm(), synonym.getTerm() });
					}
				}
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes writer", e);
			throw new SemanticMarkupException(task);
		}
	}

	private void createCategorizationFile(Task task, Collection collection, String destination) throws SemanticMarkupException {
		String path = destination + File.separator + "category_term-task-" + task.getName() + ".csv";
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not create file", e);
			throw new SemanticMarkupException(task);
		}
		
		try(CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
			csvWriter.writeNext(new String[] {"category", "term"});
			for(Label label : collection.getLabels()) {
				for(Term term : label.getTerms())
					csvWriter.writeNext(new String[] {label.getName(), term.getTerm()});
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not open or cloes writer", e);
			throw new SemanticMarkupException(task);
		}
		
		/*ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		String jsonCollection = writer.writeValueAsString(collection);
		try (FileWriter fileWriter = new FileWriter(new File(path))) {
			fileWriter.write(jsonCollection);
		}*/
	}	

	@Override
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
				return descriptions;
			}
			
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("//description", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> descriptionElements = xp.evaluate(doc);
			if(descriptionElements != null) 
				for(Element descriptionElement : descriptionElements) {
					String type = descriptionElement.getAttributeValue("type");
					String text = descriptionElement.getValue();
					descriptions.add(new Description(text, type));
				}
		}
		return descriptions;
	}
	
	@Override
	public Description getDescription(AuthenticationToken token, String filePath, int descriptionNumber) {
		List<Description> descriptions = getDescriptions(token, filePath);
		if(descriptions.size() > descriptionNumber)
			return descriptions.get(descriptionNumber);
		return null;
	}
	
	@Override
	public void setDescription(AuthenticationToken authenticationToken, String filePath, int descriptionNumber, String description) throws SemanticMarkupException {	
		String content;
		try {
			content = fileAccessService.getFileContent(authenticationToken, filePath);
		} catch (PermissionDeniedException | GetFileContentFailedException e) {
			throw new SemanticMarkupException();
		}
		String newContent;
		try {
			newContent = replaceDescription(content, description, descriptionNumber);
		} catch (JDOMException | IOException e1) {
			throw new SemanticMarkupException();
		}
		try {
			fileAccessService.setFileContent(authenticationToken, filePath, newContent);
		} catch (SetFileContentFailedException | PermissionDeniedException e) {
			throw new SemanticMarkupException();
		}
	}
	
	@Override
	public Task goToTaskStage(AuthenticationToken authenticationToken, Task task, TaskStageEnum taskStageEnum) {		
		TaskType taskType = daoManager.getTaskTypeDAO().getTaskType(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP);
		TaskStage taskStage = daoManager.getTaskStageDAO().getSemanticMarkupTaskStage(taskStageEnum.toString());
		task.setTaskStage(taskStage);
		task.setResumable(true);
		task.setComplete(false);
		task.setCompleted(null);
		daoManager.getTaskDAO().updateTask(task);
		return task;
	}

	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {		
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getOwnedTasks(user.getId());
		for(Task task : tasks) {
			if(task !=  null && task.isResumable() && !task.isFailed() && 
				task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP)) {
					//SemanticMarkupConfiguration configuration = daoManager.getSemanticMarkupConfigurationDAO().getSemanticMarkupConfiguration(task.getConfiguration().getConfiguration().getId());
					return task;
			}
		}
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task) throws SemanticMarkupException {		
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
			
		//remove matrix generation configuration
		if(config != null) {
			if(config.getInput() != null)
				try {
					fileService.setInUse(authenticationToken, false, config.getInput(), task);
				} catch (PermissionDeniedException e) {
					throw new SemanticMarkupException(task);
				}
			daoManager.getSemanticMarkupConfigurationDAO().remove(config);
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
				case LEARN_TERMS:
					if(activeLearnFutures.containsKey(config.getConfiguration().getId())) {
						ListenableFuture<LearnResult> learnFuture = activeLearnFutures.get(config.getConfiguration().getId());
						learnFuture.cancel(true);
					}
					if(activeLearns.containsKey(config.getConfiguration().getId())) {
						activeLearns.get(config.getConfiguration().getId()).destroy();
					}
					break;
				case OUTPUT:
					break;
				case PARSE_TEXT:
					if(activeParseFutures.containsKey(config.getConfiguration().getId())) {
						ListenableFuture<ParseResult> parseFuture = activeParseFutures.get(config.getConfiguration().getId());
						parseFuture.cancel(true);
					}
					if(activeParses.containsKey(config.getConfiguration().getId())) {
						activeParses.get(config.getConfiguration().getId()).destroy();
					}
					break;
				case PREPROCESS_TEXT:
					break;
				case REVIEW_TERMS:
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void destroy() {
		this.executorService.shutdownNow();
		for(Learn learn : activeLearns.values()) {
			learn.destroy();
		}
		for(Parse parse : activeParses.values()) {
			parse.destroy();
		}
		super.destroy();
	}

	//TODO: Could also access the servlet instead of using oto's client
	private void createOTOContexts(AuthenticationToken authenticationToken, Task task, LearnResult learnResult, String input) throws SemanticMarkupException {
		edu.arizona.biosemantics.oto.client.oto2.Client client = new edu.arizona.biosemantics.oto.client.oto2.Client(Configuration.oto2Url);
		client.open();
		List<Context> contexts = new LinkedList<Context>();
		
		List<String> files = new LinkedList<String>();
		try {
			files = fileService.getDirectoriesFiles(authenticationToken, input);
		} catch (PermissionDeniedException e) {
			throw new SemanticMarkupException(task);
		}
		int i=0;
		for(String file : files) {
			List<Description> descriptions = getDescriptions(authenticationToken, input + File.separator + file);
			for(Description description : descriptions) {
				try {
					contexts.add(new Context(i++, getTaxonIdentification(authenticationToken, input + File.separator + file), 
							description.getContent()));
				} catch (PermissionDeniedException | GetFileContentFailedException e) {
					throw new SemanticMarkupException(task);
				}
			}
		}
		try {
			List<Context> result = client.putContexts(learnResult.getOtoUploadId(), learnResult.getOtoSecret(), contexts).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new SemanticMarkupException(task);
		}
		client.close();
	}
	
	private void sendFinishedLearningTermsEmail(Task task) {
		User user = daoManager.getUserDAO().getUser(task.getUser().getId());
		if (user.getProfileValue(User.EmailPreference.TEXT_CAPTURE.getKey())) {
			String subject = Configuration.finishedSemanticMarkupLearnSubject.replace("<taskname>", task.getName());
			String body = Configuration.finishedSemanticMarkupLearnBody.replace("<taskname>", task.getName());
			emailer.sendEmail(user.getEmail(), subject, body);
		}
	}
	
	private void sendFinishedParsingEmail(Task task) {
		User user = daoManager.getUserDAO().getUser(task.getUser().getId());
		if (user.getProfileValue(User.EmailPreference.TEXT_CAPTURE.getKey())) {
			String subject = Configuration.finishedSemanticMarkupParseSubject.replace("<taskname>", task.getName());
			String body = Configuration.finishedSemanticMarkupParseBody.replace("<taskname>", task.getName());
			emailer.sendEmail(user.getEmail(), subject, body);
		}
	}
	
	private void sendFailedParsingEmail(Task task, String input, String output) {
		User user = daoManager.getUserDAO().getUser(task.getUser().getId());
		if (user.getProfileValue(User.EmailPreference.TEXT_CAPTURE.getKey())) {
			String subject = Configuration.failedSemanticMarkupParseSubject.replace("<taskname>", task.getName());
			String body = Configuration.failedSemanticMarkupParseBody.replace("<taskname>", task.getName());
			body=body.replace("<user>", user.getEmail());
			emailer.sendEmailAttachment("hong1.cui@gmail.com", subject, body,input,output);
		}
	}
	
	private String getNumberOfSentences() {
		return "XXXXX";
		//return 5423;
	}

	private String getNumberOfWords() {
		return "XXXXX";
		//return 21259;
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
				List<Element> strainNumbers = new LinkedList<Element>();
				for(Element taxonIdentification : taxonIdentifications) {
					if(taxonIdentification.getAttributeValue("status").equalsIgnoreCase("accepted")) {
						if(!taxonIdentification.getChildren("taxon_name").isEmpty()) {
							taxonNames.addAll(taxonIdentification.getChildren("taxon_name"));
						}
						if(!taxonIdentification.getChildren("strain_number").isEmpty()) {
							strainNumbers.addAll(taxonIdentification.getChildren("strain_number"));
						}
					}
				}
				List<Element> sources = sourceXPath.evaluate(doc);
				return createTaxonIdentification(sources.get(0), taxonNames, strainNumbers);
			}
		}
		return null;
	}
	
	private String createTaxonIdentification(Element source, List<Element> taxonNames, List<Element> strainNumbers) {
		String result = "";
		//StringBuilder taxonNameBuilder = new StringBuilder();

		//the whole set of classes dealing with taxonomy building from plain text
		//should be moved in an own small project: functionality is currently shared
		//between matrix-generation, matrix-review, etc-site (for oto2)
		LinkedList<RankData> rankDatas = new LinkedList<RankData>();
		Map<Rank, RankData> rankDataMap = new HashMap<Rank, RankData>();
		for(Element taxonName : taxonNames) {
			String authority = taxonName.getAttributeValue("authority");
			String date = taxonName.getAttributeValue("date");
			Rank rank = Rank.valueOf(taxonName.getAttributeValue("rank").toUpperCase());
			String name = taxonName.getValue();
			RankData rankData = new RankData(rank, name, authority, date);
			rankDatas.add(rankData);
			rankDataMap.put(rank, rankData);
			//taxonNameBuilder.append(taxonName.getAttributeValue("rank") + "=" + taxonName.getValue() + ",");
		}
		Collections.sort(rankDatas);
		if(!rankDatas.isEmpty()){
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
			} else 
				result = rankDatas.getLast().getName();
			
			String rankAuthor = rankDatas.getLast().getAuthor();
			if(rankAuthor == null || rankAuthor.isEmpty())
				rankAuthor = "unknown";
			String rankDate = rankDatas.getLast().getDate();
			if(rankDate == null || rankDate.isEmpty())
				rankDate = "unknown";
			result += " sec. " + rankAuthor + ", " + rankDate ;
		}
		if(!strainNumbers.isEmpty()){
			for(Element strainNumber : strainNumbers) {
				String strainnumber = strainNumber.getValue();
				result += " " + strainnumber + "";
			}
		};
		
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
        
		result +=  " (from "+ sourceString + ")";
		return result;
	}

	private String replaceDescription(String content, String description, int descriptionNumber) throws JDOMException, IOException {
		SAXBuilder sax = new SAXBuilder();
		try(StringReader reader = new StringReader(content)) {
			Document doc = sax.build(reader);
			
			XPathFactory xpfac = XPathFactory.instance();
			XPathExpression<Element> xp = xpfac.compile("//description", Filters.element(), null,
					Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> descriptions = xp.evaluate(doc);
			descriptions.get(descriptionNumber).setText(description);
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			return outputter.outputString(doc);
		}
	}

	private List<PreprocessedDescription> getPreprocessedDescriptions(AuthenticationToken authenticationToken, Task task) {
		List<PreprocessedDescription> result = new LinkedList<PreprocessedDescription>();
		final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		String inputDirectory = config.getInput();
		
		boolean isDirectory;
		try {
			isDirectory = fileService.isDirectory(authenticationToken, inputDirectory);
		} catch (PermissionDeniedException e) {
			return result;
		}
		if(isDirectory) {
			List<String> directoryFiles;
			try {
				directoryFiles = fileService.getDirectoriesFiles(authenticationToken, inputDirectory);
			} catch (PermissionDeniedException e) {
				return result;
			}
			for(String file : directoryFiles) {
				List<Description> descriptions = getDescriptions(authenticationToken, inputDirectory + File.separator + file);
				for(int descriptionNumber = 0; descriptionNumber<descriptions.size(); descriptionNumber++) {
					String text = descriptions.get(descriptionNumber).getContent();
					if(!bracketValidator.validate(text)) {
						PreprocessedDescription preprocessedDescription = new PreprocessedDescription(
								inputDirectory + File.separator + file, file, descriptionNumber,
										bracketValidator.getBracketCountDifferences(text));
						result.add(preprocessedDescription);
					}
				}
			}
		}
		return result;
	}
	
	private SemanticMarkupConfiguration getSemanticMarkupConfiguration(Task task) {
		final AbstractTaskConfiguration configuration = task.getConfiguration();
		if(!(configuration instanceof SemanticMarkupConfiguration))
			return null;
		return (SemanticMarkupConfiguration)configuration;
	}


	@Override
	public void renameTerm(AuthenticationToken token, Task task, String term, String newName) {
		SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		final String input = config.getInput();
		List<String> files = new LinkedList<String>();
		try {
			files = fileService.getDirectoriesFiles(token, input);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Couldn't access files of the task to rename terms", e);
		}
		for(String file : files) {
			List<Description> descriptions = getDescriptions(token, input + File.separator + file);
			for(int descriptionNumber = 0; descriptionNumber<descriptions.size(); descriptionNumber++) {
				String text = descriptions.get(descriptionNumber).getContent();
				text = text.replaceAll("(?i)\\b" + term + "\\b", newName);
				try {
					setDescription(token, input + File.separator + file, descriptionNumber, text);
				} catch (SemanticMarkupException e) {
					log(LogLevel.ERROR, "Couldnt' set description upon rename term", e);
				}
			}
		}
		
		//replace in DB of learn because parse step picks up from there
		renameTermInSemanticMarkupDB(task, term, newName);
	}

	private void renameTermInSemanticMarkupDB(Task task, String term, String newName) {
		SemanticMarkupDBDAO semanticMarkupDBDAO = daoManager.getSemanticMarkupDBDAO();
		semanticMarkupDBDAO.renameTerm(String.valueOf(task.getId()), term, newName);
	}

	@Override
	public void sendToOto(AuthenticationToken token, Task task) throws Exception {
		SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		User user = daoManager.getUserDAO().getUser(token.getUserId());
		Collection collection = collectionService.get(config.getOtoUploadId(), config.getOtoSecret());
		otoSender.send(task, config, user, collection);
		config.setOtoCreatedDataset(true);
		daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(config);
	}

	@Override
	public List<Task> getResumables(AuthenticationToken authenticationToken) {
		List<Task> result = new LinkedList<Task>();
		ShortUser user = daoManager.getUserDAO().getShortUser(authenticationToken.getUserId());
		List<Task> tasks = daoManager.getTaskDAO().getResumableTasks(user.getId());
		for(Task task : tasks) {
			if(task != null && task.isResumable() && !task.isFailed() && 
					task.getTaskType().getTaskTypeEnum().equals(edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum.SEMANTIC_MARKUP)) {
				result.add(task);
			}
		}
		return result;
	}

	@Override
	public boolean isLargeInput(AuthenticationToken token, String inputFile) {
		int numWords = 0;
		
		File inputDir = new File(inputFile);
		for (File file: inputDir.listFiles()){
			for (Description description : getDescriptions(token, file.getAbsolutePath())){
				numWords += description.getContent().split(" ").length;
			}
		}
		
		return numWords > Configuration.semanticMarkup_numberOfWordsToWarnUser;
	}
}
