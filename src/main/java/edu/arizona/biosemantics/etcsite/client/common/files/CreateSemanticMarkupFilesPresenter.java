package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView.IConfirmListener;
import edu.arizona.biosemantics.etcsite.client.common.IMessageView;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.server.rpc.XmlModelFileCreator;
//import edu.arizona.biosemantics.etcsite.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.shared.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.file.MyXmlWriter;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.BracketChecker;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ParallelRPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.ParentRPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class CreateSemanticMarkupFilesPresenter implements ICreateSemanticMarkupFilesView.Presenter {

	private ICreateSemanticMarkupFilesView view;
	private IFileServiceAsync fileService;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private IMessageView.Presenter messagePresenter;
	private IMessageConfirmView.Presenter messageConfirmPresenter;
	private FilePathShortener filePathShortener = new FilePathShortener();
	private int filesCreated;
	private String destinationFilePath;
	private BracketChecker bracketChecker = new BracketChecker();
	
	
	@Inject
	public CreateSemanticMarkupFilesPresenter(ICreateSemanticMarkupFilesView view, IFileServiceAsync fileService, 
			IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService,  IMessageView.Presenter messagePresenter, IMessageConfirmView.Presenter messageConfirmPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileService = fileService;
		this.fileAccessService = fileAccessService;
		this.fileFormatService = fileFormatService;
		this.messagePresenter = messagePresenter;
		this.messageConfirmPresenter = messageConfirmPresenter;
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
					messagePresenter.showMessage("Input Error", error.replaceAll("\n", "</br>"));
				}
			}
		});
	}

	private void createXmlFiles(final List<XmlModelFile> modelFiles, final String destinationFilePath) {
		List<ParallelRPCCallback> parallelRPCCallbacks = new LinkedList<ParallelRPCCallback>();
		for(final XmlModelFile modelFile : modelFiles) {
			ParallelRPCCallback parallelRPCCallback = new ParallelRPCCallback<String>() {
				@Override
				public void onResult(final String data) {
					if(data.isEmpty()){
						setFailure();
					}else{
						final String content = modelFile.getXML();
						fileAccessService.setFileContent(Authentication.getInstance().getToken(), data, content, new RPCCallback<Void>() {
							@Override
							public void onResult(Void result) {
								callSuperOnResult(data);
							}
						});
					}
				}

				private void callSuperOnResult(String result) {
					super.onResult(result);
				}
			};
			parallelRPCCallbacks.add(parallelRPCCallback);
		}
		ParentRPCCallback parentCallback = new ParentRPCCallback(parallelRPCCallbacks) {
			@Override
			protected void handleSuccess() {
				StringBuilder messageBuilder = new StringBuilder();
				int count = 0;
				for(int i=0; i<modelFiles.size(); i++) {
					if(!this.getCallbackFailureState(i)){ 
						messageBuilder.append("File " +
							filePathShortener.shortenOwnedPath(
									destinationFilePath + ServerSetup.getInstance().getSetup().getSeperator() + getCallbackData(i)) + " " +
									"successfully created in " + filePathShortener.shortenOwnedPath(destinationFilePath) + "\n");
					count++;
					}
				}
				if(count>0) {
					messagePresenter.showMessage(count+" file(s) successfully created", messageBuilder.toString().replace("\n", "<br>"));
					//filesCreated += modelFiles.size();
					filesCreated += count;
				}
			}
			@Override
			protected void handleFailure() {
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
					messagePresenter.showMessage(count+" file(s) not created", messageBuilder.toString().replace("\n", "<br>"));
				}
			}
		};
		
		for(int i=0; i<modelFiles.size(); i++) {
			XmlModelFile modelFile = modelFiles.get(i);
			//create empty file if content is valid
			fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, modelFile.getFileName(), modelFile.getXML(),
					true, parallelRPCCallbacks.get(i));
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
	public void onBatch(String text) {
		fileFormatService.createTaxonDescriptionFile(Authentication.getInstance().getToken(), text, 
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
					messagePresenter.showMessage("Input Error", error.replaceAll("\n", "</br>"));
				}
			}
		});
	}
}
