package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.fileManager.DirectoryTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTree;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class FileTreePresenter implements Presenter, IFileMoveListener {

	public interface Display {
		FileImageLabelTree getTree();
		Widget asWidget();
	}

	private IFileServiceAsync fileService;
	private Display display;
	private HandlerManager eventBus;
	private boolean enableDragAndDrop;
	private FileFilter fileFilter;
	
	private FileSelectionHandler fileSelectionHandler = new FileSelectionHandler();
	private FileDragDropHandler fileDragDropHandler = new FileDragDropHandler();
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	public FileTreePresenter(HandlerManager eventBus, Display display,
			IFileServiceAsync fileService, boolean enableDragAndDrop, FileFilter fileFilter) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		this.enableDragAndDrop = enableDragAndDrop;
		this.fileFilter = fileFilter;
		bind();
	}
	
	private void bind() {
		display.getTree().addSelectionHandler(fileSelectionHandler);	
		refresh();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}
	
	public void refresh() {
		loadingPopup.start();
		
		this.fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), fileFilter, 
				new AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>>() {
				public void onSuccess(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result) {
					//retain "open" state of folders
					Map<String, Boolean> retainedStates = getRetainedStates();
					
					//retain selection
					String selectionTarget = fileSelectionHandler.getTarget();
					
					//remove previous data
					display.getTree().clear();
					
					//decorate
					FileTreeDecorator fileTreeDecorator = new FileTreeDecorator();
					fileDragDropHandler.addListener(FileTreePresenter.this);
					
					if(enableDragAndDrop) 
						fileTreeDecorator.decorate(display.getTree(), result, fileDragDropHandler, selectionTarget, retainedStates);
					else
						fileTreeDecorator.decorate(display.getTree(), result, null, selectionTarget, retainedStates);
					
					loadingPopup.stop();
				}
				private Map<String, Boolean> getRetainedStates() {
					Map<String, Boolean> retainedStates = new HashMap<String, Boolean>();
					for(int i=0; i<display.getTree().getItemCount(); i++) {
						FileImageLabelTreeItem fileImageLabelTreeItem = display.getTree().getItem(i);
						fillChildrenStates(fileImageLabelTreeItem, retainedStates);
					}
					return retainedStates;
				}
				private void fillChildrenStates(FileImageLabelTreeItem fileImageLabelTreeItem, Map<String, Boolean> retainedStates) {
					retainedStates.put(fileImageLabelTreeItem.getPath(), fileImageLabelTreeItem.getState());
					for(int i=0; i < fileImageLabelTreeItem.getChildCount(); i++) {
						FileImageLabelTreeItem child = fileImageLabelTreeItem.getChild(i);
						fillChildrenStates(child, retainedStates);
					}
				}
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			}
		);
	}

	@Override
	public void notifyFileMove() {
		this.refresh();
	}
	
	public FileSelectionHandler getFileSelectionHandler() { 
		return this.fileSelectionHandler;
	}

}
