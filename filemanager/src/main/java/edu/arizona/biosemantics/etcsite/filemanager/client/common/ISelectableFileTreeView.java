package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.filemanager.client.common.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;

public interface ISelectableFileTreeView extends IsWidget {

	public interface Presenter {

		void onSelect();

		void onClose();

		ISelectableFileTreeView getView();

		void show(String title, FileFilter fileFilter,
				ISelectListener listener);

		void hide();

		IFileTreeView.Presenter getFileTreePresenter();
		
	}

	void setPresenter(Presenter presenter);
	
}
