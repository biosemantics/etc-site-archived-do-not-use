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
import edu.arizona.biosemantics.etcsite.shared.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.file.MyXmlWriter;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.BracketChecker;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModelFileCreator;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ParallelRPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.ParentRPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class CreateSemanticMarkupFilesPresenter implements ICreateSemanticMarkupFilesView.Presenter {

	private ICreateSemanticMarkupFilesView view;
	private IFileServiceAsync fileService;
	private IFileAccessServiceAsync fileAccessService;
	private IMessageView.Presenter messagePresenter;
	private IMessageConfirmView.Presenter messageConfirmPresenter;
	private FilePathShortener filePathShortener = new FilePathShortener();
	private XmlModelFileCreator xmlModelFileCreator = new XmlModelFileCreator();
	private int filesCreated;
	private String destinationFilePath;
	private BracketChecker bracketChecker = new BracketChecker();
	
	
	@Inject
	public CreateSemanticMarkupFilesPresenter(ICreateSemanticMarkupFilesView view, IFileServiceAsync fileService, 
			IFileAccessServiceAsync fileAccessService, IMessageView.Presenter messagePresenter, IMessageConfirmView.Presenter messageConfirmPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileService = fileService;
		this.fileAccessService = fileAccessService;
		this.messagePresenter = messagePresenter;
		this.messageConfirmPresenter = messageConfirmPresenter;
	}

	public void onCreate() {
		XmlModelFile modelFile = createModelFile();
		if(!modelFile.hasError()) {
			List<XmlModelFile> modelFiles = new LinkedList<XmlModelFile>();
			modelFiles.add(modelFile);
			createXmlFiles(modelFiles, destinationFilePath);
		} else 
			messagePresenter.showMessage("Input Error", modelFile.getError().replaceAll("\n", "</br>"));
	}
	
	private XmlModelFile createModelFile() {
		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append("author: " + view.getAuthor() + "\n");
		textBuilder.append("year: " + view.getYear() + "\n");
		textBuilder.append("title: " + view.getTitleText() + "\n");
		textBuilder.append("doi: " + view.getDOI() + "\n");
		textBuilder.append("full citation: " + view.getFullCitation() + "\n");
		List<TaxonIdentificationEntry> taxonIdentificationEntries = view.getTaxonIdentificationEntries();
		for(TaxonIdentificationEntry taxonIdentificationEntry : taxonIdentificationEntries) {
			textBuilder.append(taxonIdentificationEntry.getRank() + ": " + taxonIdentificationEntry.getValue() + "\n");
		}
		textBuilder.append("strain: " + view.getStrain() + "\n");
		textBuilder.append("strain source: " + view.getStrainsSource() + "\n");
		textBuilder.append("morphology: " + view.getMorphologicalDescription() + "\n");
		textBuilder.append("habitat: " + view.getHabitatDescription() + "\n");
		textBuilder.append("distribution: " + view.getDistributionDescription() + "\n");
		textBuilder.append("phenology: " + view.getPhenologyDescription() + "\n");		
		
		XmlModelFile result = xmlModelFileCreator.createXmlModelFile(textBuilder.toString(), Authentication.getInstance().getUsername());
		return result;
	}

	private void createXmlFiles(final List<XmlModelFile> modelFiles, final String destinationFilePath) {
		List<ParallelRPCCallback> parallelRPCCallbacks = new LinkedList<ParallelRPCCallback>();
		for(final XmlModelFile modelFile : modelFiles) {
			ParallelRPCCallback parallelRPCCallback = new ParallelRPCCallback<String>() {
				@Override
				public void onResult(final String data) {
					// TODO validate
					final String content = modelFile.getXML();
					fileAccessService.setFileContent(Authentication.getInstance().getToken(), data, content, new RPCCallback<Void>() {
						@Override
						public void onResult(Void result) {
							callSuperOnResult(data);
						}
					});
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
				for(int i=0; i<modelFiles.size(); i++) {
					messageBuilder.append("File " +
							filePathShortener.shortenOwnedPath(
									destinationFilePath + ServerSetup.getInstance().getSetup().getSeperator() + getCallbackData(i)) + " " +
									"successfully created in " + filePathShortener.shortenOwnedPath(destinationFilePath) + "\n");
				}
				messagePresenter.showMessage("File created", messageBuilder.toString().replace("\n", "<br>"));
				filesCreated += modelFiles.size();
			}
		};
		
		for(int i=0; i<modelFiles.size(); i++) {
			XmlModelFile modelFile = modelFiles.get(i);
			fileService.createFile(Authentication.getInstance().getToken(), destinationFilePath, modelFile.getFileName(), 
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
		List<XmlModelFile> xmlModelFiles = xmlModelFileCreator.createXmlModelFiles(text, Authentication.getInstance().getUsername());
		
		StringBuilder overallError = new StringBuilder();
		
		for(XmlModelFile xmlModelFile : xmlModelFiles) {
			if(xmlModelFile.hasError())
				overallError.append(xmlModelFile.getError() + "\n\n");
		}
		
		String error = overallError.toString();
		if(error.isEmpty())
			createXmlFiles(xmlModelFiles, destinationFilePath);
		else {
			error += "Did not create any files";
			messagePresenter.showMessage("Input Error", error.replaceAll("\n", "</br>"));
		}
	}
}
