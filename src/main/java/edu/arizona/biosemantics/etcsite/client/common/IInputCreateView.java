package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.UploadCompleteHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.Uploader;

public interface IInputCreateView extends IsWidget {
	
	public interface InputValidator {
		public void validate(String inputFolderPath);
	}
	
	public interface UploadCompleteHandler {
		void handle(FileUploadHandler fileUploadHandler, IUploader uploader, String uploadDirectory);
	}
	
	public interface Presenter {

		void onNext();

		void onSelectExistingFolder();

		void onFileManager();
		
		String getInputFolderPath();
		
		String getInputFolderShortenedPath();
		
		boolean createNewFolder(String path);

		void createFiles(FileInfo selectedFolder);

		void createFilesInNewFolder();
	
		void setInputValidator(InputValidator inputValidator);

		IInputCreateView getView();

		void disableCreateFiles();

		void refreshFolders();

		void setUploadCompleteHandler(UploadCompleteHandler handler);

		void setUploadFileType(FileTypeEnum fileType);

	}

	void setOwnedFolders(List<FileInfo> folders);

	boolean isCreateFiles();
	
	Uploader getUploader();

	Button getUploadButton();

	FileInfo getSelectedFolderForUpload();

	void setStatusWidget(Widget widget);
	
	boolean isCreateFolderForCreateFiles();

	boolean isSelectExistingFolder();

	boolean isSelectFolderForUpload();

	FileInfo getSelectedFolderForCreateFiles();

	void setSelectedExistingFolder(String shortendPath);

	void setNextButtonText(String text);

	void setPresenter(Presenter presenter);

	boolean isUpload();

	boolean isCreateFolderForUpload();

	void removeCreateFiles();

	boolean isSelectFolderForCreateFiles();

}
