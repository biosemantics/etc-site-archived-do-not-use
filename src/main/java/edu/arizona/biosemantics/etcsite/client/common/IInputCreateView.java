package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public interface IInputCreateView extends IsWidget {
	
	public interface InputValidator {
		public void validate(String inputFolderPath);
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

	}

	void setOwnedFolders(List<FileInfo> folders);

	boolean isCreateFiles();
	
	Uploader getUploader();

	Button getUploadButton();

	FileInfo getSelectedFolderForUpload();

	void setStatusWidget(Widget widget);

	void enableNextButton(boolean value);

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

}
