package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatServiceAsync;

public class FileContentPresenter implements IFileContentView.Presenter {

	private IFileContentView view;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private FileInfo currentFileInfo;
	private FileTypeEnum fileType; 
	private String currentXmlSchema;
	private Dialog dialog;

	@Inject
	public FileContentPresenter(IFileContentView view, IFileAccessServiceAsync fileAccessService,  IFileFormatServiceAsync fileFormatService) {
		this.view = view;
		view.setPresenter(this);
		this.fileAccessService = fileAccessService;
		this.fileFormatService = fileFormatService;
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeadingText("File Content");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.getButton(PredefinedButton.OK).setText("Close");

		dialog.add(view);
	}

	public void show(FileInfo fileInfo) {
		this.currentFileInfo = fileInfo;
		//this.dialogBox.setText("File Content of "+path);
		this.dialog.show();
		Alerter.startLoading();
		fileType = FileTypeEnum.getEnum(view.getFormat());
		fileAccessService.getFileContent(Authentication.getInstance().getToken(), 
				currentFileInfo.getFilePath(), fileType, new FileContentCallback(true));
	}
	
	@Override
	public void onFormatChange(String format) {
		Alerter.startLoading();
		fileType = FileTypeEnum.getEnum(format);
		fileAccessService.getFileContent(
				Authentication.getInstance().getToken(), 
				currentFileInfo.getFilePath(), fileType, new FileContentCallback(false));
	}
	
	@Override
	public void onClose() {
		dialog.hide();
	}
	
	@Override
	public void onSave(){
		fileFormatService.isValidXmlContentForSchema(Authentication.getInstance().getToken(), view.getText(), this.currentXmlSchema, 
				new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){
				if(!result.booleanValue()){
					Alerter.notSavedInvalidXmlContent();
				}else{
					//user could have possibly manipulated the schema url
					fileFormatService.setSchema(Authentication.getInstance().getToken(), view.getText(), currentFileInfo.getFileType(), 
							new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							fileAccessService.setFileContent(Authentication.getInstance().getToken(), currentFileInfo.getFilePath(), 
									result, new FileContentSaveCallback());
						}
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToSetSchema(caught);
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToValidateXmlContentForSchema(caught);
			}
		});
	}
	
	@Override
	public void onEdit(){
		if(currentFileInfo.isEditable()) {
			fileFormatService.getSchema(Authentication.getInstance().getToken(), currentFileInfo.getFilePath(), new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					currentXmlSchema = result;
					view.setEditable(true);
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToGetSchema(caught);
				}
			});
		} else {
			Alerter.fileNotEditable();
		}
	}

	private class FileContentSaveCallback implements AsyncCallback<Void>{
		public FileContentSaveCallback(){}
		
		@Override
		public void onSuccess(Void result){
			Alerter.fileSaved();
		}
		@Override
		public void onFailure(Throwable caught){
			Alerter.failedToSetFileContent(caught);
		}
		
	}
	
	
	
	private class FileContentCallback implements AsyncCallback<String> {
		private boolean center;
		public FileContentCallback(boolean center) {
			this.center = center;
		}
		@Override
		public void onSuccess(String result) {
			view.setText(result);
			view.setEditable(false);
			if(center)
				dialog.show();
			Alerter.stopLoading();
		}
		@Override
		public void onFailure(Throwable caught) {
			Alerter.failedToGetFileContent(caught);
			Alerter.stopLoading();
		}
	}

	
}
