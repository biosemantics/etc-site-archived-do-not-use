package edu.arizona.biosemantics.etcsite.client.common.files;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.model.RPCResult;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.model.process.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.model.process.file.XmlModelFileCreator;
import edu.arizona.biosemantics.etcsite.shared.model.process.semanticmarkup.BracketChecker;
//import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ParallelRPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.ParentRPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class CreateSemanticMarkupFilesPresenter implements ICreateSemanticMarkupFilesView.Presenter {

	public class TaxonDescriptionCreateParallelRPCCallback extends ParallelRPCCallback<List<XmlModelFile>> {
		private String text;
		private String error;
		private List<XmlModelFile> xmlModelFiles;
		public TaxonDescriptionCreateParallelRPCCallback(String text) {
			this.text = text;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			setFailure();
			super.onFailure(caught);
		}
		@Override
		public void onResult(final List<XmlModelFile> xmlModelFiles) {
			this.xmlModelFiles = xmlModelFiles;
			StringBuilder overallError = new StringBuilder();
			for(XmlModelFile xmlModelFile : xmlModelFiles) {
				if(xmlModelFile.hasError())
					overallError.append(xmlModelFile.getError() + "\n\n");
			}
			error = overallError.toString();
			super.onResult(xmlModelFiles);
			view.incrementProgress(parent.getDoneCount() / ((double)parent.getCount() * 2));
		}
	}
	
	public class FileCreateParallelRPCCallback extends ParallelRPCCallback<Boolean> {
		
		private String filePath;
		private XmlModelFile modelFile;
		
		public FileCreateParallelRPCCallback(XmlModelFile modelFile) {
			this.modelFile = modelFile;
		}
		
		public String getFilePath() {
			return filePath;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			setFailure();
			super.onFailure(caught);
		}
		@Override
		public void onResult(final Boolean formatResult) {
			fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, modelFile.getFileName(), 
					true, new AsyncCallback<RPCResult<String>>() {
						@Override
						public void onFailure(Throwable caught) {
							callSuperOnFailure(caught);
						}
						@Override
						public void onSuccess(RPCResult<String> data) {
							final String content = modelFile.getXML();
							filePath = data.getData();
							fileAccessService.setFileContent(Authentication.getInstance().getToken(), filePath, content, 
									new AsyncCallback<RPCResult<Void>>() {
										@Override
										public void onFailure(Throwable caught) {
											callSuperOnFailure(caught);
										}

										@Override
										public void onSuccess(RPCResult<Void> result) {
											callSuperOnResult(formatResult);
										}
							});
						}
			});
		}
		
		private void callSuperOnFailure(Throwable caught) {
			super.onFailure(caught);
			
		}
		private void callSuperOnResult(Boolean result) {
			super.onResult(result);
			view.incrementProgress((parent.getCount() + parent.getDoneCount()) / ((double)parent.getCount() * 2));
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
		List<ParallelRPCCallback> parallelRPCCallbacks = new LinkedList<ParallelRPCCallback>();
		for(final XmlModelFile modelFile : modelFiles) {
			parallelRPCCallbacks.add(new FileCreateParallelRPCCallback(modelFile));
		}
		ParentRPCCallback parentCallback = new ParentRPCCallback(parallelRPCCallbacks) {
			@Override
			protected void handleSuccess() {
				view.hideProgress();
				StringBuilder messageBuilder = new StringBuilder();
				int count = 0;
				for(int i=0; i<modelFiles.size(); i++) {
					if(!this.getCallbackFailureState(i)){ 
						messageBuilder.append("File " +
							filePathShortener.shortenOwnedPath(
									((FileCreateParallelRPCCallback)this.getChildCallbacks().get(i)).getFilePath()) + " " +
									"successfully created in " + filePathShortener.shortenOwnedPath(destinationFilePath) + "\n");
					count++;
					}
				}
				if(count>0) {
					messagePresenter.showOkBox(count+" file(s) successfully created", messageBuilder.toString().replace("\n", "<br>"));
					//filesCreated += modelFiles.size();
					filesCreated += count;
				}
			}
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
					messagePresenter.showOkBox(count+" file(s) not created", messageBuilder.toString().replace("\n", "<br>"));
				}
			}
		};
		
		for(int i=0; i<modelFiles.size(); i++) {
			final XmlModelFile modelFile = modelFiles.get(i);
			final ParallelRPCCallback callback = parallelRPCCallbacks.get(i);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					fileFormatService.isValidTaxonDescriptionContent(Authentication.getInstance().getToken(), modelFile.getXML(), callback);
				}
			});
		}
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
		view.showProgress();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				String normalizedText = xmlModelFileCreator.normalizeText(text);
				List<String> treatments = xmlModelFileCreator.getTreatmentTexts(normalizedText);
				
				final List<ParallelRPCCallback> parallelRPCCallbacks = new LinkedList<ParallelRPCCallback>();
				for(final String treatmentText : treatments) {
					parallelRPCCallbacks.add(new TaxonDescriptionCreateParallelRPCCallback(treatmentText));
				}
				ParentRPCCallback parentCallback = new ParentRPCCallback(parallelRPCCallbacks) {
					@Override
					protected void handleFailure() { 
						System.out.println("failure");
					}
					@Override
					protected void handleSuccess() {
						StringBuilder overallError = new StringBuilder();
						
						final List<XmlModelFile> xmlModelFiles = new LinkedList<XmlModelFile>();
						for(ParallelRPCCallback child : this.getChildCallbacks()) {
							TaxonDescriptionCreateParallelRPCCallback taxonDescriptionCreateParallelRPCCallback = (TaxonDescriptionCreateParallelRPCCallback)child;
							
							if(taxonDescriptionCreateParallelRPCCallback.error.isEmpty())
								xmlModelFiles.addAll(taxonDescriptionCreateParallelRPCCallback.xmlModelFiles);
							else
								overallError.append(taxonDescriptionCreateParallelRPCCallback.error + "\n");
						}
						
						String error = overallError.toString();
						if(error.isEmpty())
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									createXmlFiles(xmlModelFiles, destinationFilePath);
								}
							});
						else {
							error += "Did not create any files";
							messagePresenter.showOkBox("Input Error", error.replaceAll("\n", "</br>"));
						}	
					}
				};
				
				for(int i=0; i<treatments.size(); i++) {
					final String treatment = treatments.get(i);
					final ParallelRPCCallback callback = parallelRPCCallbacks.get(i);
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), treatment, callback);
						}
					});
				}
			}
		});		
	}
}
