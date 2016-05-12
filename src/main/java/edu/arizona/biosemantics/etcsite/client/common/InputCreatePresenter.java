package edu.arizona.biosemantics.etcsite.client.common;

import java.util.LinkedList;
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
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.ICreateSemanticMarkupFilesDialogView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.MyUploaderConstants;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPlace;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
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
	private FolderTreeItem ownedFileInfo;
	private InputValidator inputValidator;
	private UploadCompleteHandler uploadCompleteHandler;
	private FileTypeEnum uploadFileType;
	private PlaceController placeController;
		
	@Inject
	public InputCreatePresenter(PlaceController placeController, IInputCreateView view,
			ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter,
			FilePathShortener filePathShortener,
			IFileServiceAsync fileService,
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.placeController = placeController;
		this.view = view;
		view.setPresenter(this);
		this.createSemanticMarkupFilesDialogPresenter = createSemanticMarkupFilesDialogPresenter;
		this.fileService = fileService;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.filePathShortener = filePathShortener;

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
				} else {
					setInputFolderPath(createdFolderForCreateFiles);
					if(inputValidator != null)  inputValidator.validate(inputFolderPath);
				}
			} else if(view.isSelectFolderForCreateFiles()) {
				if (view.getSelectedFolderForCreateFiles() == null) {
					Alerter.selectValidInputDirectory();
				} else {
					setInputFolderPath(view.getSelectedFolderForCreateFiles().getFilePath());
					if(inputValidator != null)  inputValidator.validate(inputFolderPath);
				}
			}
			else Alerter.selectValidInputDirectory();
		} 
		
		else if (view.isUpload()) {
			if (view.isCreateFolderForUpload()) {
				if (createdFolderForUpload == null) {
					Alerter.selectValidInputDirectory();
				} else {
					setInputFolderPath(createdFolderForUpload);
					if(inputValidator != null)  inputValidator.validate(inputFolderPath);
				}
			} else if(view.isSelectFolderForUpload()) {
				if (view.getSelectedFolderForUpload() == null) {
					Alerter.selectValidInputDirectory();
				} else {
					setInputFolderPath(view.getSelectedFolderForUpload().getFilePath());
					if(inputValidator != null)  inputValidator.validate(inputFolderPath);
				}
			}
			else Alerter.selectValidInputDirectory();
		} 
		
		else if(view.isSelectExistingFolder()) {
			if (inputFolderPath == null) {
				Alerter.selectValidInputDirectory();
			}
			else {if(inputValidator != null)  inputValidator.validate(inputFolderPath);}
		}
		
		else Alerter.selectValidInputDirectory();
		
	}
	
	private void setInputFolderPath(String inputFolderPath) {
		this.inputFolderPath = inputFolderPath;
		inputFolderShortenedPath = filePathShortener.shortenOwnedPath(inputFolderPath);
	}

	@Override
	public void createFiles(final FolderTreeItem selectedFolder) {
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
	public void refreshinput() {
		this.view.refreshinput();
	}
	
	@Override
	public void deleteFolderForinputFiles() {
		this.createdFolderForCreateFiles=null;	
         this.createdFolderForUpload=null;
         this.inputFolderPath=null;
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
		fileService.getOwnedRootFolder(Authentication.getInstance().getToken(), new AsyncCallback<FolderTreeItem>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToRetrieveFiles();
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(FolderTreeItem result) {
				ownedFileInfo = result;
				final MessageBox box2 = Alerter.startLoading();
				fileService.getFiles(Authentication.getInstance().getToken(), ownedFileInfo, FileFilter.ALL, new AsyncCallback<List<FileTreeItem>>() {
					@Override
					public void onSuccess(List<FileTreeItem> result) {
						List<FolderTreeItem> folders = new LinkedList<FolderTreeItem>();
						for(FileTreeItem fileTreeItem : result)
							if(fileTreeItem instanceof FolderTreeItem)
								folders.add((FolderTreeItem)fileTreeItem);
						view.setOwnedFolders(folders);
						Alerter.stopLoading(box2);
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(box2);
						Alerter.failedToRetrieveFiles();
					}
				});
				Alerter.stopLoading(box);
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
				    view.activiatecreateButton1();
				if(view.isUpload() && view.isCreateFolderForUpload()){
					createdFolderForUpload = result;
					view.activiateuploadButton1();
				}
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
				//Alerter.successfullyUploadedFiles();
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
			public void onSelect()  {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					inputFolderPath = selection.getFilePath();
					inputFolderShortenedPath = filePathShortener.shorten(selection, Authentication.getInstance().getUserId());
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					} else if(selection.getText().contains("[0 files")) {
						Alerter.emptyFolder();
					} else if(!selection.getText().matches(".*?\\b0 director.*")){
					Alerter.containSubFolder();
				    }
					else {
						view.setSelectedExistingFolder(inputFolderShortenedPath);
						if(selection.getOwnerUserId() != Authentication.getInstance().getUserId()) {
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
	public void disableDummyCreateFiles1() {
		view.removeDummyCreateFiles1();
	}
	
	@Override
	public void disableDummyCreateFiles2() {
		view.removeDummyCreateFiles2();
	}
	
	@Override
	public void addDummyCreateFiles1() {
		view.addDummyCreateFiles1();
	}
	
	@Override
	public void addDummyCreateFiles2() {
		view.addDummyCreateFiles2();
	}
	
	@Override
	public void addDummyCreateFiles3() {
		view.addDummyCreateFiles3();
	}
	
	@Override
	public void setUploadCompleteHandler(UploadCompleteHandler handler) {
		this.uploadCompleteHandler = handler;
	}

	@Override
	public void setUploadFileType(FileTypeEnum uploadFileType) {
		this.uploadFileType = uploadFileType;
	}

	@Override
	public void setNextButtonName(String str) {
		view.setNextButtonName(str);
	}

	@Override
	public void onTextCapture() {
		placeController.goTo(new SemanticMarkupInputPlace());
	}

	@Override
	public void onMatrixGeneration() {
		placeController.goTo(new MatrixGenerationInputPlace());
	}

}