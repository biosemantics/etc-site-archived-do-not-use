package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatServiceAsync;

public class FileContentPresenter implements IFileContentView.Presenter {

	private IFileContentView view;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private FileTreeItem currentFileTreeItem;
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
		dialog.setHeading("File Content");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.getButton(PredefinedButton.OK).setText("Close");

		dialog.add(view);
	}

	public void show(FileTreeItem fileTreeItem) {
		this.currentFileTreeItem = fileTreeItem;
		//this.dialogBox.setText("File Content of "+path);
		this.dialog.show();
		final MessageBox box = Alerter.startLoading();
		fileType = FileTypeEnum.getEnum(view.getFormat());
		fileAccessService.getFileContent(Authentication.getInstance().getToken(), 
				currentFileTreeItem.getFilePath(), fileType, new FileContentCallback(true, box));
	}
	
	@Override
	public void onFormatChange(String format) {
		final MessageBox box = Alerter.startLoading();
		fileType = FileTypeEnum.getEnum(format);
		fileAccessService.getFileContent(
				Authentication.getInstance().getToken(), 
				currentFileTreeItem.getFilePath(), fileType, new FileContentCallback(false, box));
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
					Alerter.notSavedInvalidXmlContent(currentFileTreeItem.getFileType());
				}else{
					//user could have possibly manipulated the schema url
					fileFormatService.setSchema(Authentication.getInstance().getToken(), view.getText(), currentFileTreeItem.getFileType(), 
							new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							fileAccessService.setFileContent(Authentication.getInstance().getToken(), currentFileTreeItem.getFilePath(), 
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
		if(currentFileTreeItem.isEditable()) {
			fileFormatService.getSchema(Authentication.getInstance().getToken(), currentFileTreeItem.getFilePath(), new AsyncCallback<String>() {
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
		private MessageBox box;
		public FileContentCallback(boolean center, MessageBox box) {
			this.center = center;
			this.box = box;
		}
		@Override
		public void onSuccess(String result) {
			view.setText(result);
			view.setEditable(false);
			if(center)
				dialog.show();
			Alerter.stopLoading(box);
		}
		@Override
		public void onFailure(Throwable caught) {
			Alerter.failedToGetFileContent(caught);
			Alerter.stopLoading(box);
		}
	}

	
}
