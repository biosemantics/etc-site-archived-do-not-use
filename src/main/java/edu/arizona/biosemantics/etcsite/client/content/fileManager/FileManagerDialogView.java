package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.client.common.files.IManagableFileTreeView;

public class FileManagerDialogView implements IFileManagerDialogView {

	Dialog dialog;
	IManagableFileTreeView managableFileTreeView;
	
	@Inject
	public FileManagerDialogView(IManagableFileTreeView.Presenter managableFileTreePresenter) {
		this.managableFileTreeView = managableFileTreePresenter.getView();
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("File Manager");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.add(managableFileTreeView.asWidget());
	}

	@Override
	public void show() {
		dialog.show();
	}

	@Override
	public void hide() {
		dialog.hide();
	}

}
