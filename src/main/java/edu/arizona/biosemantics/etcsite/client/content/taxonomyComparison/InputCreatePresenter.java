package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.ICreateSemanticMarkupFilesDialogView;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.MyUploaderConstants;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IInputCreateView.InputValidator;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IInputCreateView.UploadCompleteHandler;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.Uploader;

public class InputCreatePresenter implements IInputCreateView.Presenter {
	
	private IFileServiceAsync fileService;
	private ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private FilePathShortener filePathShortener;
	
	private String defaultServletPath;
	private String targetUploadDirectory;
	private FileUploadHandler fileUploadHandler;
	private IInputCreateView view;
	private String cleanTaxInputFolderPath;
	private String modelInputText1;
	private String modelInputText2;
	private String modelInputFolderPath1;
	private String modelInputFolderPath2;
	private String cleanTaxInputFolderShortenedPath;
	private String modelInputFolderShortenedPath1;
	private String modelInputFolderShortenedPath2;
	private String createdFolderForCreateFiles;
	private String createdFolderForUpload;
	private InputValidator cleanTaxInputValidator;
	private InputValidator modelInputValidator;
	private UploadCompleteHandler uploadCompleteHandler;
	private FileTypeEnum uploadFileType;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private FolderTreeItem ownedFileInfo;
		
	@Inject
	public InputCreatePresenter(PlaceController placeController, final IInputCreateView view,
			ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter,
			FilePathShortener filePathShortener,
			IFileServiceAsync fileService,
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			@Named("TaxonomySelection")ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.createSemanticMarkupFilesDialogPresenter = createSemanticMarkupFilesDialogPresenter;
		this.fileService = fileService;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.filePathShortener = filePathShortener;
		this.taxonomyComparisonService = taxonomyComparisonService;

		createUploader();
	}

	private void createUploader() {
		Uploader uploader = view.getUploader();
		defaultServletPath = uploader.getServletPath();
		uploader.setServletPath(defaultServletPath);
		this.fileUploadHandler = new FileUploadHandler(fileService);
		uploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {
			@Override
			public void onFinish(IUploader uploader) {	
				String serverResponse = fileUploadHandler.parseServerResponse(uploader);
				if(serverResponse != null && !serverResponse.isEmpty()) {
					Alerter.failedToUpload(serverResponse);
				} else {
					if(uploadCompleteHandler != null)
						uploadCompleteHandler.handle(fileUploadHandler, uploader, targetUploadDirectory);
					uploader.setServletPath(defaultServletPath);
					//Alerter.successfullyUploadedFiles();
					
					taxonomyComparisonService.getTaxonomies(Authentication.getInstance().getToken(), new FolderTreeItem("", "", 
							"", targetUploadDirectory, 
							"", FileTypeEnum.DIRECTORY, -1, false, false, false, null), 
							new AsyncCallback<List<String>>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToGetTaxonomiesFromUploadedFiles(caught);
						}
						@Override
						public void onSuccess(List<String> result) {
							String text = "";
							for(String taxonomy : result) {
								text += taxonomy + ", ";
							}
							view.setUploadedTaxonomies("Selected taxonomies: " + text.substring(0, text.length() - 2));
						}
					});
				}
			}
		});
		uploader.addOnStartUploadHandler(new IUploader.OnStartUploaderHandler() {
			@Override
			public void onStart(final IUploader uploader) {			
				if (view.isCreateFolderForUpload())
					targetUploadDirectory = createdFolderForUpload;
				if (view.isSelectFolderForUpload())
					targetUploadDirectory = view.getSelectedFolderForUpload().getFilePath();
				fileUploadHandler.setServletPathOfUploader(uploader, uploadFileType.displayName(), targetUploadDirectory);
			}
		});
		uploader.setI18Constants(new MyUploaderConstants() {
			@Override
			public String uploaderBrowse() {
				return "Upload Taxonomy Files into Folder";
			}
		});
		IUploadStatus statusWidget = new BaseUploadStatus();
	    statusWidget.setCancelConfiguration(IUploadStatus.DEFAULT_CANCEL_CFG);
	    uploader.setStatusWidget(statusWidget);
	    view.setStatusWidget(statusWidget.asWidget());
	    uploader.setFileInput(new MyFileInput(view.getUploadButton()));
	}

	@Override
	public void onNext() {
		if (view.isUpload()) {
			if (view.isCreateFolderForUpload()) {
				if (createdFolderForUpload == null) {
					Alerter.selectValidInputDirectoryTaxonComp();
					return;
				} else {
					setCleanTaxInputFolderPath(createdFolderForUpload);
				}
			} else if(view.isSelectFolderForUpload()) {
				if (view.getSelectedFolderForUpload() == null) {
					Alerter.selectValidInputDirectoryTaxonComp();
					return;
				} else {
					setCleanTaxInputFolderPath(view.getSelectedFolderForUpload().getFilePath());
				}
			}
			if(cleanTaxInputValidator != null)
				cleanTaxInputValidator.validate(cleanTaxInputFolderPath);
		} else if(view.isSelectExistingFolder()) {
			if (modelInputFolderPath1 == null || modelInputFolderPath2 == null) {
				Alerter.selectValidInputDirectoryTaxonComp();
				return;
			}
			else if(modelInputValidator != null) {
				modelInputValidator.validate(modelInputFolderPath1);
				modelInputValidator.validate(modelInputFolderPath2);
			}
		}
		else 
			Alerter.selectValidInputDirectoryTaxonComp();
	}
	
	private void setCleanTaxInputFolderPath(String cleanTaxInputFolderPath) {
		this.cleanTaxInputFolderPath = cleanTaxInputFolderPath;
		cleanTaxInputFolderShortenedPath = filePathShortener.shortenOwnedPath(cleanTaxInputFolderPath);
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
					cleanTaxInputFolderPath = folderPath;
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
	public void refreshinput() {
		this.view.refreshinput();
	}
	
	@Override
	public void deleteFolderForinputFiles() {
         this.createdFolderForUpload=null;
         this.modelInputFolderPath1=null;
         this.modelInputFolderPath2=null;
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
	public void onSelectExistingFolder1() {
		selectableFileTreePresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {

			@Override
			public void onSelect()  {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					modelInputText1 = selection.getText();					
					modelInputFolderPath1 = selection.getFilePath();
					modelInputFolderShortenedPath1 = filePathShortener.shorten(selection, Authentication.getInstance().getUserId());
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					} else if(modelInputText1.contains("[0 files")) {
						Alerter.emptyFolder();
					} else if(!selection.getText().matches(".*?\\b0 director.*")){
						Alerter.containSubFolder();
					} else {
						view.setSelectedExistingFolder1(modelInputFolderShortenedPath1);
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
	public void onSelectExistingFolder2() {
		selectableFileTreePresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect()  {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					modelInputText2 = selection.getText();
					modelInputFolderPath2 = selection.getFilePath();
					modelInputFolderShortenedPath2 = filePathShortener.shorten(selection, Authentication.getInstance().getUserId());
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					} else if(selection.getText().contains("[0 files")) {
						Alerter.emptyFolder();
					} else if(!selection.getText().matches(".*?\\b0 director.*")){
						Alerter.containSubFolder();
					} else {
						view.setSelectedExistingFolder2(modelInputFolderShortenedPath2);
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
	public String getModelInputFolderPath1(){
		return modelInputFolderPath1;
	}
	
	@Override
	public String getModelInputFolderPath2(){
		return modelInputFolderPath2;
	}

	@Override
	public String getCleanTaxInputFolderShortenedPath() {
		return cleanTaxInputFolderShortenedPath;
	}
	
	@Override
	public String getModelInputFolderShortenedPath1() {
		return this.modelInputFolderShortenedPath1;
	}
	
	@Override
	public String getModelInputFolderShortenedPath2() {
		return this.modelInputFolderShortenedPath2;
	}

	@Override
	public void onFileManager() {
		fileManagerDialogPresenter.show();
	}
		
	@Override
	public void setModelInputValidator(InputValidator modelInputValidator) {
		this.modelInputValidator = modelInputValidator;
	}
	
	@Override
	public void setCleanTaxInputValidator(InputValidator cleanTaxInputValidator) {
		this.cleanTaxInputValidator = cleanTaxInputValidator;
	}

	@Override
	public IInputCreateView getView() {
		return view;
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
	public String getCleanTaxInputFolderPath() {
		return this.cleanTaxInputFolderPath;
	}

	public String getModel1() {
		return modelInputText1;
	}

	public String getModel2() {
		return modelInputText2;
	}
}
