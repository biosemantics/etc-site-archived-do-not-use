package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FilePathShortener;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.ServerSetup;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;

public class FileSelectDialogClickHandler implements ClickHandler {

	private TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(true, "Select File");
	private SelectableFileTreeView view;
	private SelectableFileTreePresenter presenter;
	private FilePathShortener filePathShortener = new FilePathShortener(ServerSetup.getInstance().getSetup().getFileBase(), 
														ServerSetup.getInstance().getSetup().getSeperator());
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "Shared input files");

	public FileSelectDialogClickHandler(HandlerManager eventBus, final IFileServiceAsync fileService, FileFilter fileFilter, 
			final HasText text, final StringBuilder stringBuilder, 
			final boolean requiredToContinue, final HasEnabled enableAble) {
		view = new SelectableFileTreeView();
		presenter = new SelectableFileTreePresenter(eventBus, view, fileService, fileFilter, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileImageLabelTreeItem selection = presenter.getFileSelectionHandler().getSelection();
				if (selection != null) {
					
					text.setText(filePathShortener.shorten(selection.getFileInfo(), Authentication.getInstance().getUsername()));
					stringBuilder.setLength(0);
					stringBuilder.append(selection.getFileInfo().getFilePath());
					
					if (requiredToContinue)
						enableAble.setEnabled(true);
					
					if(!selection.getFileInfo().getOwner().equals(Authentication.getInstance().getUsername())) {
						messagePresenter.setMessage("The selected input is not owned. To start the task the files will be copied to your own space.");
						messagePresenter.go();
						dialogBox.hide();
					} else {
						dialogBox.hide();
					}
				}
			}
		}, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
	}

	@Override
	public void onClick(ClickEvent event) {
		presenter.getFileTreePresenter().refresh();
		dialogBox.setWidget(view);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		dialogBox.show();
	}
}