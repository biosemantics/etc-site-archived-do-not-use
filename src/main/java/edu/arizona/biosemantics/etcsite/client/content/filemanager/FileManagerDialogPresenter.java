package edu.arizona.biosemantics.etcsite.client.content.filemanager;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.filemanager.client.common.IManagableFileTreeView;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;

public class FileManagerDialogPresenter implements IFileManagerDialogView.Presenter {

	private IFileManagerDialogView view;
	private IManagableFileTreeView.Presenter managableFileTreePresenter;

	@Inject
	public FileManagerDialogPresenter(IFileManagerDialogView view, 
			IManagableFileTreeView.Presenter managableFileTreePresenter) {
		this.view = view;
		this.managableFileTreePresenter = managableFileTreePresenter;
	}
	
	@Override
	public void show() {
		view.show();
		managableFileTreePresenter.refresh(FileFilter.ALL);
	}

	@Override
	public void hide() {
		view.hide();
	}

}
