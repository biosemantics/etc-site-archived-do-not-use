package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;
import gwtupload.client.IUploader;

public interface IManagableFileTreeView extends IsWidget {

	public interface Presenter {

		void onCreate();

		void onRename();

		void onDelete();

		void onDownload();

		IManagableFileTreeView getView();
		
		void refresh(FileFilter fileFilter);

	}

	void setPresenter(Presenter presenter);

	IUploader getUploader();

	void setEnabledCreateDirectory(boolean value);

	void setEnabledRename(boolean value);

	void setEnabledDelete(boolean value);

	void setEnabledUpload(boolean value);

	void setStatusWidget(Widget widget);

	Button getAddButton();

}
