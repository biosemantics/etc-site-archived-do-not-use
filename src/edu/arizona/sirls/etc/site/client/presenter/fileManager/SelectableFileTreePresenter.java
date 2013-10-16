package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.common.eventbus.EventBus;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class SelectableFileTreePresenter implements Presenter {

	public interface Display {
		Button getSelectButton();
		Button getCloseButton();
		FileTreePresenter.Display getFileTreeView();
		Widget asWidget();
	}

	private Display display;
	private HandlerManager eventBus;
	private FileTreePresenter fileTreePresenter;
	private FileTreePresenter.Display fileTreeView;
	private FileSelectionHandler fileSelectionHandler;

	public SelectableFileTreePresenter(HandlerManager eventBus, Display display, 
			IFileServiceAsync fileService, FileFilter fileFilter, 
			ClickHandler selectClickHandler, ClickHandler closeClickHandler) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileTreeView = display.getFileTreeView();
		this.fileTreePresenter = new FileTreePresenter(eventBus, 
				fileTreeView, fileService, false, fileFilter);
		this.fileSelectionHandler = fileTreePresenter.getFileSelectionHandler();
		
		bind(selectClickHandler, closeClickHandler);
	}
	
	public SelectableFileTreePresenter(HandlerManager eventBus, Display display, 
			IFileServiceAsync fileService, FileFilter fileFilter) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileTreeView = display.getFileTreeView();
		this.fileTreePresenter = new FileTreePresenter(eventBus, 
				fileTreeView, fileService, false, fileFilter);
		this.fileSelectionHandler = fileTreePresenter.getFileSelectionHandler();
	}

	private void bind(ClickHandler selectClickHandler, ClickHandler closeClickHandler) {
		display.getSelectButton().addClickHandler(selectClickHandler); 
		display.getCloseButton().addClickHandler(closeClickHandler); 
	}
	
	public FileTreePresenter getFileTreePresenter() {
		return this.fileTreePresenter;
	}


	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}


	public FileSelectionHandler getFileSelectionHandler() {
		return fileSelectionHandler;
	}
	
	public void setSelectClickHandler(ClickHandler handler) {
		display.getSelectButton().addClickHandler(handler);
	}
	
	public void setCloseClickHandler(ClickHandler handler) {
		display.getCloseButton().addClickHandler(handler);
	}

}
