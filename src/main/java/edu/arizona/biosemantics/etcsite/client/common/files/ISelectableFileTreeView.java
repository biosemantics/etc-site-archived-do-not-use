package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.Presenter;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;

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
