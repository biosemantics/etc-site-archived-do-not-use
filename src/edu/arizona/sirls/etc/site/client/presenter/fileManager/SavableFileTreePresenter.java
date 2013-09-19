package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileTreePresenter.Display;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class SavableFileTreePresenter implements Presenter {

	public interface Display {
		Widget asWidget();
		Button getCloseButton();
		FileTreePresenter.Display getFileTreeView();
		TextBox getNameTextBox();
	}

	private HandlerManager eventBus;
	private Display display;
	private FileTreePresenter fileTreePresenter;
	private FileTreePresenter.Display fileTreeView;
	private FileSelectionHandler fileSelectionHandler;
	private ClickHandler saveClickHandler;

	public SavableFileTreePresenter(HandlerManager eventBus, Display display, 
			IFileServiceAsync fileService, FileFilter fileFilter, ClickHandler saveClickHandler) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileTreeView = display.getFileTreeView();
		this.fileTreePresenter = new FileTreePresenter(eventBus, 
				fileTreeView, fileService, false, fileFilter);
		this.fileSelectionHandler = fileTreePresenter.getFileSelectionHandler();
		this.saveClickHandler = saveClickHandler;
		bind();
	}
	
	private void bind() {
		display.getCloseButton().addClickHandler(saveClickHandler);
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	public FileSelectionHandler getFileSelectionHandler() {
		return fileSelectionHandler;
	}
}
