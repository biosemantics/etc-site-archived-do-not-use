package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.MyUploaderConstants;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;

public class MatrixGenerationCreatePresenter implements MatrixGenerationCreateView.Presenter{

	private IFileServiceAsync fileService;
	private IMatrixGenerationCreateView view;
	private PlaceController placeController;
	private edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.Presenter fileTreePresenter;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private FilePathShortener filePathShortener;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	
	private FileInfo parentFileInfo;
	private String inputFolderPath;
	private String inputFolderShortenedPath;
	private String uploadFiles_newFolder;
	
	private String defaultServletPath;
	private String targetUploadDirectory;
	
	List<FileInfo> allOwnedFolders;
	List<FileInfo> allFolders;
	
	@SuppressWarnings("deprecation")
	@Inject
	public MatrixGenerationCreatePresenter(IMatrixGenerationCreateView view, 
			PlaceController placeController,
			FilePathShortener filePathShortener,
			IFileServiceAsync fileService,
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter,
			IMatrixGenerationServiceAsync matrixGenerationService) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.fileService = fileService;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.filePathShortener = filePathShortener;
		this.matrixGenerationService = matrixGenerationService;
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
		matrixGenerationService.isValidInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(!result) {
					Alerter.invalidInputDirectory();
					Alerter.stopLoading();
				} else {
					placeController.goTo(new MatrixGenerationInputPlace());
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
		List<String> uploadedFiles = null;
		@SuppressWarnings("deprecation")
		@Override
		public void onFinish(IUploader uploader) {	
			serverResponse = uploader.getServerInfo().message;
			uploadedFiles = uploader.getFileInput().getFilenames();
			if(serverResponse != null && !serverResponse.isEmpty()) {
				serverResponse = serverResponse.replaceAll("\n", "<br>");
				if(serverResponse.contains("#")){ //# is used in response only when there are errors
					String responseStrings[] = serverResponse.split("#");
					responseStrings[1] = responseStrings[1].trim();
					String xmlErrorFiles[] = responseStrings[1].split("\\|");
					responseStrings[2] = responseStrings[2].trim();
					String existingFiles[] = responseStrings[2].split("\\|");
					serverResponse = responseStrings[0]+"<br>";
					if(xmlErrorFiles.length>0 && !responseStrings[1].isEmpty()){
						serverResponse += "Following files have xml format errors<br>";
					}
					int i;
					for(i=0;i<20 && i<xmlErrorFiles.length;i++){
						serverResponse += xmlErrorFiles[i] + "<br>";
					}
					int j=0;
					if(i<20){
						if(existingFiles.length>0 && !responseStrings[2].isEmpty()){
							serverResponse += "<br>Following files already exist in the folder<br>";
						}
						for(;i<20 && j<existingFiles.length; i++, j++){
							serverResponse += existingFiles[j] + "<br>";
						}
					}
					if(j<existingFiles.length-1 | i<xmlErrorFiles.length-1){
						serverResponse += "and so on.<br>";
					}
					for(i=0; i<xmlErrorFiles.length; i++){
						uploadedFiles.remove(xmlErrorFiles[i]);
					}
					for(i=0;i<existingFiles.length;i++){
						uploadedFiles.remove(existingFiles[i]);
					}
				}
				
			}else{
				serverResponse = "Files uploaded Successfully.";
			}
			
			if (uploader.getStatus() == Status.SUCCESS) {
				fileService.validateKeys(Authentication.getInstance().getToken(), targetUploadDirectory, uploadedFiles, new AsyncCallback<HashMap<String,String>>() {

					@Override
					public void onFailure(Throwable caught) {
						Alerter.inputError("Key Validation Failed.");
					}

					@Override
					public void onSuccess(final HashMap<String, String> result) {
						if(!result.isEmpty()){
							String infoMessage = "The following files have key errors and will not be parsed.<br><br>";
							String errorMessage = "";
							int allowedErrorCounts = 2;
							for(String filename: result.keySet()){
								if (allowedErrorCounts <= 0 ){
									errorMessage += "and so on.<br>";
									break;
								}
								String errorsInFile = result.get(filename);
								errorMessage += errorsInFile.replace("\n", "<br>")+"<br>";
								allowedErrorCounts--;
							}
							MessageBox box = Alerter.showKeyValidationResult(infoMessage, errorMessage);
							box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
							
								@Override
								public void onSelect(SelectEvent event) {
									Alerter.fileManagerMessage(serverResponse);
								}
							});
							box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
								
								@Override
								public void onSelect(SelectEvent event) {
									// TODO Auto-generated method stub
									fileService.deleteUploadedFiles(Authentication.getInstance().getToken(), targetUploadDirectory, uploadedFiles, new AsyncCallback<Void>() {

										@Override
										public void onFailure(
													Throwable caught) {
											// TODO Auto-generated method stub
											Alerter.inputError("Could not delete files.");
										}

										@Override
										public void onSuccess(Void result) {
												// TODO Auto-generated method stub
												view.enableNextButton(true);
										}
									});
								}
							});
							box.show();
						}else{
							Alerter.fileManagerMessage(serverResponse);
						}
					}
				});
			}else{
				Alerter.fileManagerMessage("Upload Failed.");
			}
			//targetUploadDirectory = "";			
			uploader.setServletPath(defaultServletPath);
			view.enableNextButton(true);
		}		
	}
	
	

	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(final IUploader uploader) {			
			String servletPath = view.getUploader().getServletPath() + "?fileType=" + FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION.displayName() + "&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId()))
					+ "&sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId());
			uploader.setServletPath(servletPath);
			
			List<String> fileNames = new LinkedList<String>();
			fileNames.add("Uploading, please wait...");
			uploader.getStatusWidget().setFileNames(fileNames);
			targetUploadDirectory = view.getSelectedUploadDirectory();
			uploader.setServletPath(uploader.getServletPath() + "&target=" + targetUploadDirectory);
			
			/*
			 * Creation of directories directly inside of the upload target should not be possible (possible name clash)
			 * Rename of target and files directly inside of target should not be possible (target no longer available, name clash)
			 * Delete of target should not be possible (target no longer available)
			 */
			
			// for now, just disable all of the others:
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
