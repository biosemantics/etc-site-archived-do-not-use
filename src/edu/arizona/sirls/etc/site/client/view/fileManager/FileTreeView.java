package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileTreePresenter;

public class FileTreeView extends Composite implements FileTreePresenter.Display {

	private final Tree tree = new Tree();
	
	public FileTreeView() { 
		initWidget(tree);
	}

	@Override
	public Tree getTree() {
		return tree;
	}
	
}
