package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public interface ITaxonomyComparisonCreateView extends IsWidget {

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
	  
	public void setPresenter(Presenter presenter);
	public void setOwnedFolderNames(List<FileInfo> folders);
	public void setCreateFolderStatus(String string);
	public boolean getUploadRadioValue();	
	public Uploader getUploader();
	public Button getUploadButton();
	public String getSelectedUploadDirectory();
	public void setStatusWidget(Widget widget);
	public void enableNextButton(boolean value);	
	public  Boolean getNewFolderRadio_upload();
	public Boolean getSelectFolderRadio_upload();
	public FileInfo getSelectFolderComboBox_upload();
	public void setSelectedFolder(String shortendPath);
	public void setFilePath(String shortendPath);
	public void setEnabledNext(boolean b);

}
