package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class FileTreeViewImpl extends Composite implements FileTreeView {

	private static FileTreeViewUiBinder uiBinder = GWT.create(FileTreeViewUiBinder.class);

	@UiTemplate("FileTreeView.ui.xml")
	interface FileTreeViewUiBinder extends UiBinder<Widget, FileTreeViewImpl> {
	}

	@UiField
	FileImageLabelTree fileTree;
	private Presenter presenter;
	
	public FileTreeViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public FileImageLabelTree getTree() {
		return fileTree;
	}
	
}
