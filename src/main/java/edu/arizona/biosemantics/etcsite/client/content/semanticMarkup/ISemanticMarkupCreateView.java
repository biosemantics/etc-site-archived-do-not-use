package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public interface ISemanticMarkupCreateView extends IsWidget{
	
	public interface Presenter {

		IsWidget getView();

		void onNext();

		void getAllFolders();

		boolean createNewFolder(String text);

		void createFiles(FileInfo createdFolder);

		void createFilesInNewFolder();

		String getInputFolder();
		
	}
	
	void setPresenter(Presenter presenter);

	void setFolderNames(List<FileInfo> result);

	void setCreateFolderStatus(String string);
	
	boolean getCreateRadioValue();
	
	boolean getUploadRadioValue();
	
	Uploader getUploader();

	Button getUploadButton();

	String getSelectedUploadDirectory();

	void setStatusWidget(Widget widget);

	void enableNextButton(boolean value);
	
	public boolean getNewFolderRadio_create();

	public Boolean getSelectFolderRadio_create();

	public FileInfo getSelectFolderComboBox_create();

	public Boolean getNewFolderRadio_upload();

	public Boolean getSelectFolderRadio_upload();

	public FileInfo getSelectFolderComboBox_upload();
}
