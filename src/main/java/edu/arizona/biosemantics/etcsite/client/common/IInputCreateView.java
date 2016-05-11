package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
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

		void createFiles(FolderTreeItem selectedFolder);

		void createFilesInNewFolder();
	
		void setInputValidator(InputValidator inputValidator);

		IInputCreateView getView();

		void disableCreateFiles();

        void disableDummyCreateFiles1(); 
        
        void disableDummyCreateFiles2(); 
		
		void addDummyCreateFiles1();
		
		void addDummyCreateFiles2();
		
		void addDummyCreateFiles3();
		
		void setNextButtonName(String str);

		void refreshFolders();
		
		void refreshinput();

		void setUploadCompleteHandler(UploadCompleteHandler handler);

		void setUploadFileType(FileTypeEnum fileType);
		
		void deleteFolderForinputFiles();

		void onTextCapture();

		void onMatrixGeneration();

	}

	void setOwnedFolders(List<FolderTreeItem> folders);

	boolean isCreateFiles();
	
	Uploader getUploader();

	Button getUploadButton();

	FolderTreeItem getSelectedFolderForUpload();

	void setStatusWidget(Widget widget);
	
	boolean isCreateFolderForCreateFiles();

	boolean isSelectExistingFolder();

	boolean isSelectFolderForUpload();

	FolderTreeItem getSelectedFolderForCreateFiles();

	void setSelectedExistingFolder(String shortendPath);

	void setNextButtonText(String text);

	void setPresenter(Presenter presenter);

	boolean isUpload();

	boolean isCreateFolderForUpload();
	
	void activiateuploadButton1();

	void removeCreateFiles();
	
	void removeDummyCreateFiles1();
	
	void removeDummyCreateFiles2();
	
	void addDummyCreateFiles1();
	
	void addDummyCreateFiles2();

	boolean isSelectFolderForCreateFiles();

	void setNextButtonName(String str);
	
	void resetCreate();
	
	void resetUpload();
	
	void resetSelectExisting();
	
	void refreshinput();

	void addDummyCreateFiles3();

	void activiatecreateButton1();
	

}
