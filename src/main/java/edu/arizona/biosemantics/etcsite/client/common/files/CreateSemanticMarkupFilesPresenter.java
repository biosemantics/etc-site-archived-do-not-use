package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.model.process.file.XmlModelFileCreator;
import edu.arizona.biosemantics.etcsite.shared.model.process.semanticmarkup.BracketChecker;
//import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class CreateSemanticMarkupFilesPresenter implements ICreateSemanticMarkupFilesView.Presenter {

	public class TaxonDescriptionCreateRPCCallback extends RPCCallback<List<XmlModelFile>> {
		private List<String> treatments;
		private int i;
		private StringBuilder overallError;
		private List<XmlModelFile> overallXmlModelFiles;
		public TaxonDescriptionCreateRPCCallback(List<String> treatments, int i, StringBuilder overallError, List<XmlModelFile> overallXmlModelFiles) {
			this.treatments = treatments;
			this.i = i;
			this.overallError = overallError;
			this.overallXmlModelFiles= overallXmlModelFiles;
		}
		@Override
		public void onFailure(Throwable caught) {
			Alerter.failedToCreateTaxonDescription(caught);
		}
		@Override
		public void onResult(final List<XmlModelFile> xmlModelFiles) {
			StringBuilder errorOfThisBatch = new StringBuilder();
			for(XmlModelFile xmlModelFile : xmlModelFiles) {
				if(xmlModelFile.hasError())
					errorOfThisBatch.append(xmlModelFile.getError() + "\n\n");
			}
			String error = errorOfThisBatch.toString();
			if(error.isEmpty())
				overallXmlModelFiles.addAll(xmlModelFiles);
			else 
				overallError.append(error + "\n");
			
			view.updateProgress(i / ((double)treatments.size() * 4));
			
			if(treatments.size() - 1 > i) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), treatments.get(i + 1), 
								new TaxonDescriptionCreateRPCCallback(treatments, i+1, overallError, overallXmlModelFiles));
					}
					
				});
			} else {
				String overallErrorString = overallError.toString();
				if(overallErrorString.isEmpty())
					createXmlFiles(overallXmlModelFiles, destinationFilePath);
				else {
					overallErrorString += "Did not create any files";
					messagePresenter.showOkBox("Input Error", overallErrorString.replaceAll("\n", "</br>"));
					view.hideProgress();
				}
			}
		}
	}
	
	public class FileCreateRPCCallback extends RPCCallback<Boolean> {
		
		private List<XmlModelFile> modelFiles;
		private int i;
		private StringBuilder messageBuilder;
		
		public FileCreateRPCCallback(List<XmlModelFile> modelFiles, int i, StringBuilder messageBuilder) {
			this.modelFiles = modelFiles;
			this.i = i;
			this.messageBuilder = messageBuilder;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			Alerter.failedToCheckValidityOfTaxonDescription(caught);
			
			/* TODO don't alert on single callback, instead collect them and alert when finished recursively clalling service in else branch
										@Override
										protected void handleFailure() {
											view.hideProgress();
											StringBuilder messageBuilder = new StringBuilder();
											messageBuilder.append("Some of the files are not validated against the <a href='https://raw.githubusercontent.com/biosemantics/schemas/master/consolidation_01272014/semanticMarkupInput.xsd'>schema</a>.\n");
											messageBuilder.append("These files were not created. Correct the input and try again:\n");
											int count = 0;
											for(int i=0; i<modelFiles.size(); i++) {
												if(this.getCallbackFailureState(i)){ 
												messageBuilder.append(modelFiles.get(i).getFileName().replaceFirst("\\.\\w+$", "").replaceAll("_", ",") +"\n");
												count++;
												}
											}
											if(count>0){
												mess
										}
			 */
		}
		@Override
		public void onResult(final Boolean formatResult) {
			view.updateProgress((i * 3 + modelFiles.size()) / ((double)modelFiles.size() * 4));
			
			final XmlModelFile modelFile = modelFiles.get(i);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, modelFile.getFileName(), 
							true, new AsyncCallback<RPCResult<String>>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToCreateFile(caught);
									view.hideProgress();
								}
								@Override
								public void onSuccess(RPCResult<String> data) {
									view.updateProgress(((i * 3) + 1 + modelFiles.size()) / ((double)modelFiles.size() * 4));
									final String content = modelFile.getXML();
									final String filePath = data.getData();
									
									Scheduler.get().scheduleDeferred(new ScheduledCommand() {

										@Override
										public void execute() {
											fileAccessService.setFileContent(Authentication.getInstance().getToken(), filePath, content, 
													new AsyncCallback<RPCResult<Void>>() {
														@Override
														public void onFailure(Throwable caught) {
															Alerter.failedToSetContent(caught);
															view.hideProgress();
														}
														@Override
														public void onSuccess(RPCResult<Void> result) {
															view.updateProgress(((i * 3) + 2 + modelFiles.size()) / ((double)modelFiles.size() * 4));
															/*messageBuilder.append("File " +
																	filePathShortener.shortenOwnedPath(filePath) + " " +
																			"successfully created in " + filePathShortener.shortenOwnedPath(destinationFilePath) + "\n");
															*/										
															if(modelFiles.size() - 1 > i) {
																Scheduler.get().scheduleDeferred(new ScheduledCommand() {

																	@Override
																	public void execute() {
																		fileFormatService.isValidTaxonDescriptionContent(Authentication.getInstance().getToken(), 
																				modelFiles.get(i + 1).getXML(), new FileCreateRPCCallback(modelFiles, i + 1, messageBuilder));
																	}
																});
															} else {
																view.updateProgress(1.0);
																view.hideProgress();
																int count =  modelFiles.size();
																messagePresenter.showOkBox("File creation", count+" file(s) successfully created</br>" +  
																		messageBuilder.toString().replace("\n", "<br>"));
																filesCreated += count;
															}
														}
											});
										}
										
									});
									
								}
						});
					}
				
			});
		}
			
	};
	
	private ICreateSemanticMarkupFilesView view;
	private IFileServiceAsync fileService;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private FilePathShortener filePathShortener = new FilePathShortener();
	private int filesCreated;
	private String destinationFilePath;
	private BracketChecker bracketChecker = new BracketChecker();
	private XmlModelFileCreator xmlModelFileCreator = new XmlModelFileCreator();
	private MessagePresenter messagePresenter = new MessagePresenter();
	
	@Inject
	public CreateSemanticMarkupFilesPresenter(ICreateSemanticMarkupFilesView view, IFileServiceAsync fileService, 
			IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService) {
		this.view = view;
		view.setPresenter(this);
		this.fileService = fileService;
		this.fileAccessService = fileAccessService;
		this.fileFormatService = fileFormatService;
	}

	public void onCreate() {
		createModelFile();
	}
	
	private void createModelFile() {
		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append("author: " + view.getAuthor().trim() + "\n");
		textBuilder.append("year: " + view.getYear().trim() + "\n");
		textBuilder.append("title: " + view.getTitleText().trim() + "\n");
		textBuilder.append("doi: " + view.getDOI().trim() + "\n");
		textBuilder.append("full citation: " + view.getFullCitation().trim() + "\n");
		List<TaxonIdentificationEntry> taxonIdentificationEntries = view.getTaxonIdentificationEntries();
		for(TaxonIdentificationEntry taxonIdentificationEntry : taxonIdentificationEntries) {
			String rank = taxonIdentificationEntry.getRank().trim();
			String name = taxonIdentificationEntry.getValue().trim();
			if(!rank.isEmpty() && !name.isEmpty()){
				textBuilder.append(rank + " name: " + name + "\n");
			}
		}
		textBuilder.append("strain number: " + view.getStrainNumber().trim() + "\n");
		textBuilder.append("equivalent strain numbers: " + view.getEqStrainNumbers().trim() + "\n");
		textBuilder.append("accession number 16s rrna: " + view.getStrainAccession().trim() + "\n");
		
		textBuilder.append("morphology: #" + view.getMorphologicalDescription().trim() + "#\n");
		textBuilder.append("habitat: #" + view.getHabitatDescription().trim() + "#\n");
		textBuilder.append("distribution: #" + view.getDistributionDescription().trim() + "#\n");
		textBuilder.append("phenology: #" + view.getPhenologyDescription().trim() + "#\n");		
		
		fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), textBuilder.toString(), 
				new RPCCallback<List<XmlModelFile>>() {
			@Override
			public void onResult(List<XmlModelFile> modelFiles) {
				StringBuilder overallError = new StringBuilder();
				
				for(XmlModelFile xmlModelFile : modelFiles) {
					if(xmlModelFile.hasError())
						overallError.append(xmlModelFile.getError() + "\n\n");
				}
				
				String error = overallError.toString();
				if(error.isEmpty())
					createXmlFiles(modelFiles, destinationFilePath);
				else {
					error += "Did not create any files";
					messagePresenter.showOkBox("Input Error", error.replaceAll("\n", "</br>"));
				}
			}
		});
	}

	private void createXmlFiles(final List<XmlModelFile> modelFiles, final String destinationFilePath) {
		if(modelFiles.size() > 0) {		
			final int i = 0;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					fileFormatService.isValidTaxonDescriptionContent(Authentication.getInstance().getToken(), modelFiles.get(i).getXML(), 
							new FileCreateRPCCallback(modelFiles, i, new StringBuilder()));
				}
			});
		} else
			view.hideProgress();
	}

	@Override
	public ICreateSemanticMarkupFilesView getView() {
		return view;
	}
	
	@Override
	public void setDestinationFilePath(String destinationFilePath) {
		this.filesCreated = 0;
		this.destinationFilePath = destinationFilePath;
	}

	@Override
	public int getFilesCreated() {
		return this.filesCreated;
	}

	@Override
	public void init() {
		view.removeAddtionalTaxonRanks();
	}

	@Override
	public void onBatch(final String text) {
		//TODO: Move all subsequent server calls into a single call for faster processing of huge batch text
		view.showProgress();
		
		String normalizedText = xmlModelFileCreator.normalizeText(text);
		final List<String> treatments = xmlModelFileCreator.getTreatmentTexts(normalizedText);
		
		//temporarily in place as long as we are not sure about the stability of out of memory issue
		if(treatments.size() > 50) {
			messagePresenter.showOkBox("Too many files", "Currently only uploads <= 50 files are allowed");
			view.hideProgress();
			return;
		}
		
		final StringBuilder overallError = new StringBuilder();
		final List<XmlModelFile> overallXmlModelFiles = new LinkedList<XmlModelFile>();		
		
		if(treatments.size() > 0) {		
			final int i = 0;
			fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), treatments.get(i), 
					new TaxonDescriptionCreateRPCCallback(treatments, i, overallError, overallXmlModelFiles));
		} else
			view.hideProgress();	
	}
}
