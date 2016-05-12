package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.taxonomy.Description;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.DescriptionEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.model.process.file.XmlModelFileCreator;
//import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatServiceAsync;

public class CreateSemanticMarkupFilesPresenter implements ICreateSemanticMarkupFilesView.Presenter {

	public class TaxonDescriptionCreateRPCCallback implements AsyncCallback<List<XmlModelFile>> {
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
			view.hideProgress();
		}
		@Override
		public void onSuccess(final List<XmlModelFile> xmlModelFiles) {
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
					overallErrorString += "Did not create any files! Please try again!";
					Alerter.inputError(overallErrorString.replaceAll("\n", "</br>"));
					view.hideProgress();
				}
			}
		}
	}
	
	public class FileCreateRPCCallback implements AsyncCallback<Boolean> {
		
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
		public void onSuccess(final Boolean formatResult) {
			view.updateProgress((i * 3 + modelFiles.size()) / ((double)modelFiles.size() * 4));
			
			final XmlModelFile modelFile = modelFiles.get(i);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, modelFile.getFileName(), 
							true, new AsyncCallback<String>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToCreateFile(caught);
									view.hideProgress();
								}
								@Override
								public void onSuccess(final String filePath) {
									view.updateProgress(((i * 3) + 1 + modelFiles.size()) / ((double)modelFiles.size() * 4));
									final String content = modelFile.getXML();
									
									Scheduler.get().scheduleDeferred(new ScheduledCommand() {

										@Override
										public void execute() {
											fileAccessService.setFileContent(Authentication.getInstance().getToken(), filePath, content, 
													new AsyncCallback<Void>() {
														@Override
														public void onFailure(Throwable caught) {
															Alerter.failedToSetContent(caught);
															view.hideProgress();
														}
														@Override
														public void onSuccess(Void result) {
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
																view.clearBatchText();
																int count =  modelFiles.size();
																Alerter.fileCreationSuccessful(count, messageBuilder.toString().replace("\n", "<br>"));
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
	private int filesCreated;
	private String destinationFilePath;
	private XmlModelFileCreator xmlModelFileCreator = new XmlModelFileCreator();
	private String wrongTaxonNameError = "Taxon name input should be of the form: Name Authority, Date.  If authority or date value is not known provide 'unspecified' as the value in corresponding place(s)";
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
		view.showProgress();
		createModelFile();
	}
	
	private boolean createModelFile() {
		boolean createrank = false;
		boolean createdescription = false;
		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append("author: " + shortenAuthor(view.getAuthor().trim()) + "\n");
		textBuilder.append("year: " + view.getYear().trim() + "\n");
		textBuilder.append("title: " + view.getTitleText().trim() + "\n");
		textBuilder.append("doi: " + view.getDOI().trim() + "\n");
		textBuilder.append("full citation: " + view.getFullCitation().trim() + "\n");
		List<TaxonIdentificationEntry> taxonIdentificationEntries = view.getTaxonIdentificationEntries();
		for(TaxonIdentificationEntry taxonIdentificationEntry : taxonIdentificationEntries) {
			Rank rank = taxonIdentificationEntry.getRank();
			String name = taxonIdentificationEntry.getValue().trim();
			if(rank != null && !name.isEmpty()){
				textBuilder.append(rank + " name: " + name + "\n");
				if(!xmlModelFileCreator.validateName(name)){
					Alerter.inputError(wrongTaxonNameError);
					view.hideProgress();
					return false;	
				}
				createrank = true;
			}
		}
		textBuilder.append("strain number: " + view.getStrainNumber().trim() + "\n");
		textBuilder.append("equivalent strain numbers: " + view.getEqStrainNumbers().trim() + "\n");
		textBuilder.append("accession number 16s rrna: " + view.getStrainAccession().trim() + "\n");
		textBuilder.append("accession number genome sequence: " + view.getStrainGenomeAccession().trim() + "\n");
		textBuilder.append("previous or new taxonomic names: " + view.getAlternativeTaxonomy().trim() + "\n");
		if(!createrank&&view.getStrainNumber().trim().isEmpty()) {
			Alerter.inputError("Please input at least one taxon name or one strain number in the treatment!");
			view.hideProgress();
			return false;
		}
		
		List<DescriptionEntry> descriptions  = view.getDescriptionsList();
		for(DescriptionEntry entry: descriptions){
			Description type = entry.getType();
			String scope = entry.getScope();
			String desc = entry.getDescription();
			if(type!=null && !desc.isEmpty()){
				String descriptionType = type.name();
				if(scope != null && !scope.isEmpty()){
					descriptionType = scope + "-" + descriptionType;
				}
				textBuilder.append(descriptionType + ": #" + desc + "#\n");
				createdescription = true;
			}	
		}
		if (!createdescription){
			Alerter.inputError("Please input description!");
			view.hideProgress();
			return false;
		}
		
		fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), textBuilder.toString(), 
				new AsyncCallback<List<XmlModelFile>>() {
			@Override
			public void onSuccess(List<XmlModelFile> modelFiles) {
				StringBuilder overallError = new StringBuilder();
				
				for(XmlModelFile xmlModelFile : modelFiles) {
					if(xmlModelFile.hasError())
						overallError.append(xmlModelFile.getError() + "\n\n");
				}
				
				String error = overallError.toString();
				if(error.isEmpty()){
					System.out.println(destinationFilePath);
					createXmlFiles(modelFiles, destinationFilePath);
					view.removeAddtionalTaxonRanks();
					view.resetDescriptions();
					view.resetStrain();
				}else {
					error += "Did not create any files! Please try again!";
					Alerter.inputError(error.replaceAll("\n", "</br>"));
					view.hideProgress();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToCreateTaxonDescription(caught);
				view.hideProgress();
			}
		});
		
		return true;
	}

	/**
	 * to avoid excessively long file names, shorten author info to the first author name.
	 * @param trim
	 * @return
	 */
	private String shortenAuthor(String author) {
		int endIndex = author.indexOf(" ");
		return endIndex>0? author.substring(0, endIndex) : author;
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
		view.resetDescriptions();
		view.resetStrain();
	}

	@Override
	public void onBatch(final String text) {
		//TODO: Move all subsequent server calls into a single call for faster processing of huge batch text
		view.showProgress();
		String normalizedText = xmlModelFileCreator.normalizeText(text);
		final List<String> treatments = xmlModelFileCreator.getTreatmentTexts(normalizedText);
		
		//temporarily in place as long as we are not sure about the stability of out of memory issue
		/*if(treatments.size() > 50) {
			Alerter.tooManyFiles();
			view.hideProgress();
			return;
		}*/
		
		final StringBuilder overallError = new StringBuilder();
		final List<XmlModelFile> overallXmlModelFiles = new LinkedList<XmlModelFile>();		
		
		if(treatments.size() > 0) {		
			final int i = 0;
			fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), treatments.get(i), 
					new TaxonDescriptionCreateRPCCallback(treatments, i, overallError, overallXmlModelFiles));
		} else
		view.hideProgress();	
	}
	
	@Override
	public void setPreviewText(LinkedHashMap<String, String> batchSourceDocumentInfoMap, String text) {
		String returnString = "";
		view.setPreviewText("");
		String normalizedText = xmlModelFileCreator.normalizeText(text);
		if(view.isCopyCheckBox()){
			normalizedText = xmlModelFileCreator.copyAuthorityAndDate(normalizedText);
			if(normalizedText.contains("ERROR")) {
				Alerter.inputError(normalizedText.replace("ERROR","The line \"")+ "\" is not validated. The authority and date values are missing for taxon names. Unable to copy values! Please try again!");
				return;
			}
		}
		
		try {
			xmlModelFileCreator.validateTaxonNamesFormat(normalizedText);
		} catch(Exception e) {
			Alerter.inputError("The line \"" + e.getMessage() + "\" is not validated. "+ wrongTaxonNameError);
			return;
		}
		try {
			xmlModelFileCreator.validateTaxonNames(normalizedText);
		} catch(Exception e) {
			Alerter.showAlert("Taxon name validation", "Duplicate taxon name: " + e.getMessage());
			return;
		}
		
		try {
			final List<String> treatments = xmlModelFileCreator.getTreatmentTexts(batchSourceDocumentInfoMap, normalizedText);
			for (String treatment : treatments) {
				returnString += treatment + "\n";
			}
			view.setPreviewText(returnString);
		} catch(Exception e) {
			Alerter.showAlert("Batch Input Creation", "You have malformed input. " + e.getMessage());
			return;
		}
	}

}
