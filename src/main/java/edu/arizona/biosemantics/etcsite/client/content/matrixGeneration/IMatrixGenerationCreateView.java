package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public interface IMatrixGenerationCreateView extends IsWidget{
	
	public interface Presenter {

		IsWidget getView();

		void onNext();

		void getAllFolders();

		boolean createNewFolder(String text);

		void onSelect();

		String getInputFolderPath();

		String getInputFolderShortenedPath();

		void onFileManager();
		
	}
	
	void setPresenter(Presenter presenter);

	void setOwnedFolderNames(List<FileInfo> folders);

	void setCreateFolderStatus(String string);
	
	boolean getUploadRadioValue();
	
	Uploader getUploader();

	Button getUploadButton();

	String getSelectedUploadDirectory();

	void setStatusWidget(Widget widget);

	void enableNextButton(boolean value);
	
	public Boolean getNewFolderRadio_upload();

	public Boolean getSelectFolderRadio_upload();

	public FileInfo getSelectFolderComboBox_upload();

	void setSelectedFolder(String shortendPath);
}
