package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.files.IManagableFileTreeView;

public class FileManagerDialogView implements IFileManagerDialogView {

	TitleCloseDialogBox dialogBox;
	
	IManagableFileTreeView managableFileTreeView;
	
	@Inject
	public FileManagerDialogView(IManagableFileTreeView.Presenter managableFileTreePresenter) {
		this.managableFileTreeView = managableFileTreePresenter.getView();
		this.dialogBox = new TitleCloseDialogBox(false, "File Manager");
		dialogBox.setWidget(managableFileTreeView);
		dialogBox.setGlassEnabled(true);
	}

	@Override
	public void show() {
		dialogBox.center();
	}

	@Override
	public void hide() {
		dialogBox.hide();
	}

}
