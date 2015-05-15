package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.MyUploaderConstants;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;

public class TreeGenerationCreatePresenter implements TreeGenerationCreateView.Presenter{

	private IFileServiceAsync fileService;
	private ITreeGenerationCreateView view;
	private PlaceController placeController;
	private edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.Presenter fileTreePresenter;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private FilePathShortener filePathShortener;
	private ITreeGenerationServiceAsync TreeGenerationService;
	
	private FileInfo parentFileInfo;
	private String inputFolderPath;
	private String inputFolderShortenedPath;
	private String uploadFiles_newFolder;
	
	private String defaultServletPath;
	private String targetUploadDirectory;
	
	List<FileInfo> allOwnedFolders;
	List<FileInfo> allFolders;
	
	private FileUploadHandler fileUploadHandler;
	
	@SuppressWarnings("deprecation")
	@Inject
	public TreeGenerationCreatePresenter(ITreeGenerationCreateView view, 
			PlaceController placeController,
			FilePathShortener filePathShortener,
			IFileServiceAsync fileService,
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter,
			ITreeGenerationServiceAsync TreeGenerationService) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.fileService = fileService;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.filePathShortener = filePathShortener;
		this.TreeGenerationService = TreeGenerationService;
		this.parentFileInfo = null;
		this.inputFolderPath = null;
		getAllFolders();
		
		defaultServletPath = view.getUploader().getServletPath();
		view.getUploader().setServletPath(defaultServletPath);
		view.getUploader().addOnFinishUploadHandler(new OnFinishUploadHandler());
		view.getUploader().addOnStartUploadHandler(new OnStartUploadHandler());
		view.getUploader().setI18Constants(new MyUploaderConstants());

		IUploadStatus statusWidget = new BaseUploadStatus();
	    statusWidget.setCancelConfiguration(IUploadStatus.DEFAULT_CANCEL_CFG);
	    view.getUploader().setStatusWidget(statusWidget);
	    view.setStatusWidget(statusWidget.getWidget());
		view.getUploader().setFileInput(new MyFileInput(view.getUploadButton()));
		this.fileUploadHandler = new FileUploadHandler();
	}
	
	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onNext() {
		if(view.getUploadRadioValue()){
			if(view.getNewFolderRadio_upload()){
				if(uploadFiles_newFolder == null){
					Alerter.selectValidInputDirectory();
					return;
				}else{
					inputFolderPath = uploadFiles_newFolder;
					inputFolderShortenedPath = filePathShortener.shortenOwnedPath(inputFolderPath);
				}
			}else{
				if(view.getSelectFolderComboBox_upload() == null){
					Alerter.selectValidInputDirectory();
					return;
				}else{
					inputFolderPath = view.getSelectFolderComboBox_upload().getFilePath();
					inputFolderShortenedPath = filePathShortener.shortenOwnedPath(inputFolderPath);
				}
			}
		}else{
			if (inputFolderPath == null){
				Alerter.selectValidInputDirectory();
				return;
			}
		}
		Alerter.startLoading();
		TreeGenerationService.isValidInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(!result) {
					Alerter.invalidInputDirectory();
					Alerter.stopLoading();
				} else {
					placeController.goTo(new TreeGenerationInputPlace());
					Alerter.stopLoading();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToIsValidInput(caught);
				Alerter.stopLoading();
			}
		});
	}

	@Override
	public void getAllFolders() {
		fileService.getAllOwnedFolders(Authentication.getInstance().getToken(), new AsyncCallback<List<FileInfo>>() {
			
			@Override
			public void onSuccess(List<FileInfo> result) {
				view.setOwnedFolderNames(result);
				allOwnedFolders = result;
				fileService.getOwnedRootFolder(Authentication.getInstance().getToken(), new AsyncCallback<FileInfo>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(FileInfo result) {
						parentFileInfo = result;
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	@Override
	public boolean createNewFolder(String folderName) {
		if(folderName.isEmpty()){
			Alerter.inputError("New folder name cannot be empty.");
			return false;
		}
		Alerter.startLoading();
		fileService.createDirectory(Authentication.getInstance().getToken(), parentFileInfo.getFilePath(), folderName, false, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading();
			}

			@Override
			public void onSuccess(String result) {
				uploadFiles_newFolder = result;
				Alerter.stopLoading();
				view.setCreateFolderStatus("Folder Created");
			}
		});
		return true;
	}
		
	public class OnFinishUploadHandler implements OnFinishUploaderHandler {

		String serverResponse = null;
		@Override
		public void onFinish(IUploader uploader) {	
			serverResponse = fileUploadHandler.parseServerResponse(uploader);
			if (uploader.getStatus() == Status.SUCCESS) {
				Alerter.fileManagerMessage(serverResponse);
			}
			uploader.setServletPath(defaultServletPath);
			view.enableNextButton(true);
		}		
	
	}
	
	

	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(final IUploader uploader) {			
			if(view.getNewFolderRadio_upload()){
				targetUploadDirectory = uploadFiles_newFolder;
			}else{
				targetUploadDirectory = view.getSelectedUploadDirectory();
			}
			fileUploadHandler.setServletPathOfUploader(uploader, view.getUploader(), FileTypeEnum.MATRIX.displayName(), targetUploadDirectory);
			view.enableNextButton(false);
		}
	}
	
	public class MyFileInput extends ButtonFileInput {
		public MyFileInput(Button addButton) {
			super(addButton);
		}

		
		@Override
		public void setVisible(boolean b) {
			//ignore visibility based on active upload
		}

	}
	
	@Override
	public void onSelect() {
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
						view.setSelectedFolder(inputFolderShortenedPath);
						view.enableNextButton(true);
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
}
