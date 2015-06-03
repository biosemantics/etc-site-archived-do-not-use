package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.InputValidator;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.UploadCompleteHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.ICreateSemanticMarkupFilesDialogView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.MyUploaderConstants;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.Uploader;

public class InputCreatePresenter implements IInputCreateView.Presenter {
	
	private IFileServiceAsync fileService;
	private ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter;
	private edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.Presenter fileTreePresenter;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private FilePathShortener filePathShortener;
	
	private String defaultServletPath;
	private String targetUploadDirectory;
	private FileUploadHandler fileUploadHandler;
	private IInputCreateView view;
	private String inputFolderPath;
	private String inputFolderShortenedPath;
	private String createdFolderForCreateFiles;
	private String createdFolderForUpload;
	private FileInfo ownedFileInfo;
	private InputValidator inputValidator;
	private UploadCompleteHandler uploadCompleteHandler;
	private FileTypeEnum uploadFileType;
		
	@Inject
	public InputCreatePresenter(PlaceController placeController, IInputCreateView view,
			ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter,
			FilePathShortener filePathShortener,
			IFileServiceAsync fileService,
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.createSemanticMarkupFilesDialogPresenter = createSemanticMarkupFilesDialogPresenter;
		this.fileService = fileService;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.filePathShortener = filePathShortener;

		final MessageBox box = Alerter.startLoading();
		fileService.getOwnedRootFolder(Authentication.getInstance().getToken(), new AsyncCallback<FileInfo>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToRetrieveFiles();
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(FileInfo result) {
				ownedFileInfo = result;
				Alerter.stopLoading(box);
			}
		});
		this.refreshFolders();
		
		Uploader uploader = view.getUploader();
		defaultServletPath = uploader.getServletPath();
		uploader.setServletPath(defaultServletPath);
		uploader.addOnFinishUploadHandler(new OnFinishUploadHandler());
		uploader.addOnStartUploadHandler(new OnStartUploadHandler());
		uploader.setI18Constants(new MyUploaderConstants());

		IUploadStatus statusWidget = new BaseUploadStatus();
	    statusWidget.setCancelConfiguration(IUploadStatus.DEFAULT_CANCEL_CFG);
	    uploader.setStatusWidget(statusWidget);
	    view.setStatusWidget(statusWidget.asWidget());
	    uploader.setFileInput(new MyFileInput(view.getUploadButton()));
		this.fileUploadHandler = new FileUploadHandler(fileService);
	}
	
	@Override
	public void onNext() {
		if (view.isCreateFiles()) {
			if (view.isCreateFolderForCreateFiles()) {
				if (createdFolderForCreateFiles == null) {
					Alerter.selectValidInputDirectory();
					return;
				} else {
					setInputFolderPath(createdFolderForCreateFiles);
				}
			} else if(view.isSelectFolderForCreateFiles()) {
				if (view.getSelectedFolderForCreateFiles() == null) {
					Alerter.selectValidInputDirectory();
					return;
				} else {
					setInputFolderPath(view.getSelectedFolderForCreateFiles().getFilePath());
				}
			}
		} else if (view.isUpload()) {
			if (view.isCreateFolderForUpload()) {
				if (createdFolderForUpload == null) {
					Alerter.selectValidInputDirectory();
					return;
				} else {
					setInputFolderPath(createdFolderForUpload);
				}
			} else if(view.isSelectFolderForUpload()) {
				if (view.getSelectedFolderForUpload() == null) {
					Alerter.selectValidInputDirectory();
					return;
				} else {
					setInputFolderPath(view.getSelectedFolderForUpload().getFilePath());
				}
			}
		} else if(view.isSelectExistingFolder()) {
			if (inputFolderPath == null) {
				Alerter.selectValidInputDirectory();
				return;
			}
		}
		if(inputValidator != null)
			inputValidator.validate(inputFolderPath);
	}
	
	private void setInputFolderPath(String inputFolderPath) {
		this.inputFolderPath = inputFolderPath;
		inputFolderShortenedPath = filePathShortener.shortenOwnedPath(inputFolderPath);
	}

	@Override
	public void createFiles(final FileInfo selectedFolder) {
		if(selectedFolder != null && selectedFolder.isAllowsNewFiles()) {
			showCreateFilesDialog(selectedFolder.getFilePath());
		}
		else {
			Alerter.noDestinationSelected();	
		}		
	}
	
	private void showCreateFilesDialog(final String folderPath){
		createSemanticMarkupFilesDialogPresenter.setCloseHandler(new ICloseHandler() {
			@Override
			public void onClose(int filesCreated) {
				if(filesCreated > 0)
					inputFolderPath = folderPath;
			}
		});
		createSemanticMarkupFilesDialogPresenter.show(folderPath);
	}
	
	@Override
	public void createFilesInNewFolder() {
		if(createdFolderForCreateFiles != null && !createdFolderForCreateFiles.isEmpty()){
			showCreateFilesDialog(createdFolderForCreateFiles);
		}else{
			Alerter.inputError("New Folder is not created yet. Wait and try again.");
		}
	}

	@Override
	public void refreshFolders() {
		final MessageBox box = Alerter.startLoading();
		fileService.getAllOwnedFolders(Authentication.getInstance().getToken(), new AsyncCallback<List<FileInfo>>() {
			@Override
			public void onSuccess(List<FileInfo> result) {
				view.setOwnedFolders(result);
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(box);
				Alerter.failedToRetrieveFiles();
			}
		});
	}
	
	@Override
	public boolean createNewFolder(String folderName) {
		if(folderName.isEmpty()){
			Alerter.inputError("New folder name cannot be empty.");
			return false;
		}
		final MessageBox box = Alerter.startLoading();
		fileService.createDirectory(Authentication.getInstance().getToken(), ownedFileInfo.getFilePath(), folderName, false, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(box);
				Alerter.failedToCreateDirectory(caught);
			}
			@Override
			public void onSuccess(String result) {
				if(view.isCreateFiles() && view.isCreateFolderForCreateFiles())
					createdFolderForCreateFiles = result;
				if(view.isUpload() && view.isCreateFolderForUpload())
					createdFolderForUpload = result;
				Alerter.stopLoading(box);
				Alerter.createdFolderSuccessfully();
				refreshFolders();
			}
		});
		return true;
	}
		
	protected class OnFinishUploadHandler implements OnFinishUploaderHandler {
		String serverResponse = null;
		@Override
		public void onFinish(IUploader uploader) {	
			serverResponse = fileUploadHandler.parseServerResponse(uploader);
			if(serverResponse != null && !serverResponse.isEmpty()) {
				Alerter.failedToUpload(serverResponse);
			} else {
				if(uploadCompleteHandler != null)
					uploadCompleteHandler.handle(fileUploadHandler, uploader, targetUploadDirectory);
				uploader.setServletPath(defaultServletPath);
				Alerter.successfullyUploadedFiles();
			}
		}
	
	}
	
	protected class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(final IUploader uploader) {			
			if (view.isCreateFolderForUpload())
				targetUploadDirectory = createdFolderForUpload;
			if (view.isSelectFolderForUpload())
				targetUploadDirectory = view.getSelectedFolderForUpload().getFilePath();
			fileUploadHandler.setServletPathOfUploader(uploader, uploadFileType.displayName(), targetUploadDirectory);
		}
	}
	
	protected class MyFileInput extends ButtonFileInput {
		public MyFileInput(Button addButton) {
			super(addButton);
		}		
		@Override
		public void setVisible(boolean b) {
			//ignore visibility based on active upload
		}
	}
	
	@Override
	public void onSelectExistingFolder() {
		selectableFileTreePresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect() {
				FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
				if (selection != null) {
					inputFolderPath = selection.getFileInfo().getFilePath();
					inputFolderShortenedPath = filePathShortener.shorten(selection.getFileInfo(), Authentication.getInstance().getUserId());
					if(selection.getFileInfo().isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					}else if(selection.getText().contains(" 0 file")){
						Alerter.emptyFolder();
					}else{
						view.setSelectedExistingFolder(inputFolderShortenedPath);
						if(selection.getFileInfo().getOwnerUserId() != Authentication.getInstance().getUserId()) {
							Alerter.sharedInputForTask();
							fileManagerDialogPresenter.hide();
						} else {
							fileManagerDialogPresenter.hide();
						}
					}
				}
			}
		});
	}
	
	@Override
	public String getInputFolderPath(){
		return inputFolderPath;
	}

	@Override
	public String getInputFolderShortenedPath(){
		return inputFolderShortenedPath;
	}

	@Override
	public void onFileManager() {
		fileManagerDialogPresenter.show();
	}
		
	@Override
	public void setInputValidator(InputValidator inputValidator) {
		this.inputValidator = inputValidator;
	}

	@Override
	public IInputCreateView getView() {
		return view;
	}

	@Override
	public void disableCreateFiles() {
		view.removeCreateFiles();
	}
	
	@Override
	public void setUploadCompleteHandler(UploadCompleteHandler handler) {
		this.uploadCompleteHandler = handler;
	}

	@Override
	public void setUploadFileType(FileTypeEnum uploadFileType) {
		this.uploadFileType = uploadFileType;
	}
	
}