package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import com.google.inject.Inject;

public class FileTreePresenter implements IFileTreeView.Presenter {

	private IFileTreeView view;

	@Inject
	public FileTreePresenter(IFileTreeView view) {
		this.view = view;
	}
	
	@Override
	public IFileTreeView getView() {
		return view;
	}
	
}
