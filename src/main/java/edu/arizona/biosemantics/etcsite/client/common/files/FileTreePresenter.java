package edu.arizona.biosemantics.etcsite.client.common.files;

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
