package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.MarkupReviewEvent;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.fileManager.ManagableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class FileManagerPresenter implements Presenter {

	public interface Display {
		Widget asWidget();
		void addFileTreeView(Widget fileTreeView);
		Anchor getMarkupReviewAnchor();
		SimplePanel getFileManagerPanel();
	}

	private HandlerManager eventBus;
	private Display display;
	private IFileServiceAsync fileService;
	private ManagableFileTreePresenter managableFileTreePresenter;

	public FileManagerPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		bind();
	}

	private void bind() {
		ManagableFileTreeView managableFileTreeView = new ManagableFileTreeView();
		managableFileTreePresenter = new ManagableFileTreePresenter(
				eventBus, managableFileTreeView, fileService, true, FileFilter.ALL);
		display.addFileTreeView(managableFileTreeView);
		
		display.getMarkupReviewAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new MarkupReviewEvent());
			}
		});
	}

	@Override
	public void go(HasWidgets content) {
		content.clear();
		content.add(display.asWidget());
		managableFileTreePresenter.refresh();
		ManagableFileTreePresenter.setInputFileMultiple();
	}

	public void setSelectedFile(String fileSelection) {
		managableFileTreePresenter.getFileTreePresenter().setSelectedFile(fileSelection);
	}
}
