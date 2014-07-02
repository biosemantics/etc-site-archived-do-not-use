package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.files.IManagableFileTreeView;

public class FileManagerDialogView implements IFileManagerDialogView {

	PopupPanel dialogBox;
	
	IManagableFileTreeView managableFileTreeView;
	
	@Inject
	public FileManagerDialogView(IManagableFileTreeView.Presenter managableFileTreePresenter) {
		this.managableFileTreeView = managableFileTreePresenter.getView();
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(managableFileTreeView.asWidget());
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
