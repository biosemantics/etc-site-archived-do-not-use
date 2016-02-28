package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.files.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IInputCreateView.InputValidator;
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

		void onSelectExistingFolder1();
		
		void onSelectExistingFolder2();

		void onFileManager();
		
		boolean createNewFolder(String path);

		void createFiles(FolderTreeItem selectedFolder);

		void createFilesInNewFolder();

		IInputCreateView getView();

		void setNextButtonName(String str);

		void refreshFolders();

		void setUploadCompleteHandler(UploadCompleteHandler handler);

		void setUploadFileType(FileTypeEnum fileType);

		String getModelInputFolderPath1();
		
		String getModelInputFolderPath2();

		String getCleanTaxInputFolderPath();
		
		void setModelInputValidator(InputValidator modelInputValidator);

		void setCleanTaxInputValidator(InputValidator cleanTaxInputValidator);

		String getCleanTaxInputFolderShortenedPath();
		
		String getModelInputFolderShortenedPath1();

		String getModelInputFolderShortenedPath2();

		String getModel1();
		String getModel2();

		void deleteFolderForinputFiles();

		void refreshinput();

	}

	void setOwnedFolders(List<FolderTreeItem> folders);

	Uploader getUploader();

	Button getUploadButton();

	FolderTreeItem getSelectedFolderForUpload();

	
	boolean isSelectExistingFolder();

	boolean isSelectFolderForUpload();

	void setSelectedExistingFolder1(String shortendPath);

	void setSelectedExistingFolder2(String shortendPath);
	
	void setNextButtonText(String text);

	void setPresenter(Presenter presenter);

	boolean isUpload();

	boolean isCreateFolderForUpload();

	void setNextButtonName(String str);

	void setStatusWidget(Widget asWidget);

	void setUploadedTaxonomies(String result);

	void activiateuploadButton1();

	void resetUpload();

	void resetSelectExisting();

	void refreshinput();	

}
