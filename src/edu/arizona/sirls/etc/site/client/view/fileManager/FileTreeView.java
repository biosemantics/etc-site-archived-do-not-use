package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileTreePresenter;

public class FileTreeView extends Composite implements FileTreePresenter.Display {

	private final FileImageLabelTree tree = new FileImageLabelTree();
	
	public FileTreeView() { 
		initWidget(tree);
	}

	@Override
	public FileImageLabelTree getTree() {
		return tree;
	}
	
}
