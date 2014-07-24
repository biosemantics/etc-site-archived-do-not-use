package edu.arizona.biosemantics.etcsite.client.common.files;


import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IMessageView;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.shared.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class FileContentPresenter implements IFileContentView.Presenter {

	private IFileContentView view;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private PopupPanel dialogBox;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private FileInfo currentFileInfo;
	private FileTypeEnum fileType; 
	private IMessageView.Presenter messagePresenter;
	private String currentXmlSchema;

	@Inject
	public FileContentPresenter(IFileContentView view, IFileAccessServiceAsync fileAccessService,  IFileFormatServiceAsync fileFormatService, IMessageView.Presenter messagePresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileAccessService = fileAccessService;
		this.fileFormatService = fileFormatService;
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		this.messagePresenter = messagePresenter;
	}

	public void show(FileInfo fileInfo) {
		this.currentFileInfo = fileInfo;
		//this.dialogBox.setText("File Content of "+path);
		this.dialogBox.center();
		loadingPopup.start();
		fileType = FileTypeEnum.getEnum(view.getFormat());
		fileAccessService.getFileContent(Authentication.getInstance().getToken(), 
				currentFileInfo.getFilePath(), fileType, new FileContentCallback(true, loadingPopup));
	}
	
	@Override
	public void onFormatChange(String format) {
		loadingPopup.start();
		fileType = FileTypeEnum.getEnum(format);
		fileAccessService.getFileContent(
				Authentication.getInstance().getToken(), 
				currentFileInfo.getFilePath(), fileType, new FileContentCallback(false, loadingPopup));
	}
	
	@Override
	public void onClose() {
		dialogBox.hide();
	}
	
	@Override
	public void onSave(){
		fileFormatService.isValidXmlContentForSchema(Authentication.getInstance().getToken(), view.getText(), this.currentXmlSchema, new RPCCallback<Boolean>(){
			@Override
			public void onResult(Boolean result){
				if(!result.booleanValue()){
					messagePresenter.showMessage("Not saved", "Content is no longer valid against the <a href='https://raw.githubusercontent.com/biosemantics/schemas/master/consolidation_01272014/semanticMarkupInput.xsd' target='_blank'>input schema</a>. "
							+ "correct the problems and try to save again.");
				}else{
					//user could have possibly manipulated the schema url
					fileFormatService.setSchema(Authentication.getInstance().getToken(), view.getText(), currentFileInfo.getFileType(), new RPCCallback<String>() {
						@Override
						public void onResult(String result) {
							fileAccessService.setFileContent(Authentication.getInstance().getToken(), currentFileInfo.getFilePath(), result, new FileContentSaveCallback());
						}
					});
				}
			}
		});
	}
	
	@Override
	public void onEdit(){
		if(currentFileInfo.isEditable()) {
			fileFormatService.getSchema(Authentication.getInstance().getToken(), currentFileInfo.getFilePath(), new RPCCallback<String>() {
				@Override
				public void onResult(String result) {
					currentXmlSchema = result;
					view.setEditable(true);
				}
			});
		} else {
			messagePresenter.showMessage("File not editable", "This file type can't be edited at this time.");
		}
	}

	private class FileContentSaveCallback extends RPCCallback<Void>{
		public FileContentSaveCallback(){}
		
		@Override
		public void onResult(Void result){
			System.out.println("should not land here");
		}
		@Override
		public void onSuccess(RPCResult<Void> result){
			messagePresenter.showMessage("File saved", "File saved successfully.");
		}
		
		@Override
		public void onFailure(Throwable caught){
			messagePresenter.showMessage("File not saved", "Internal error.");
		}
		
	}
	
	
	
	private class FileContentCallback extends RPCCallback<String> {
		private boolean center;
		public FileContentCallback(boolean center, LoadingPopup loadingPopup) {
			super(loadingPopup);
			this.center = center;
		}
		@Override
		public void onResult(String result) {
			view.setText(result);
			view.setEditable(false);
			if(center)
				dialogBox.center();
		}
	}

	
}
