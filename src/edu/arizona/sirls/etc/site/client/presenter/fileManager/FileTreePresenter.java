package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class FileTreePresenter implements Presenter, FileTreeView.Presenter, IFileMoveListener {

	private IFileServiceAsync fileService;
	private FileTreeView view;
	private HandlerManager eventBus;
	private boolean enableDragAndDrop;
	private FileFilter fileFilter;
	
	private FileSelectionHandler fileSelectionHandler = new FileSelectionHandler();
	private FileDragDropHandler fileDragDropHandler = new FileDragDropHandler();
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	public FileTreePresenter(HandlerManager eventBus, FileTreeView view,
			IFileServiceAsync fileService, boolean enableDragAndDrop, FileFilter fileFilter) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		this.fileService = fileService;
		this.enableDragAndDrop = enableDragAndDrop;
		this.fileFilter = fileFilter;
		bind();
	}
	
	private void bind() {
		view.getTree().addSelectionHandler(fileSelectionHandler);	
		refresh();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
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
					view.getTree().clear();
					
					//decorate
					FileTreeDecorator fileTreeDecorator = new FileTreeDecorator();
					fileDragDropHandler.addListener(FileTreePresenter.this);
					
					if(enableDragAndDrop) 
						fileTreeDecorator.decorate(view.getTree(), result, fileDragDropHandler, selectionTarget, retainedStates);
					else
						fileTreeDecorator.decorate(view.getTree(), result, null, selectionTarget, retainedStates);
					
					loadingPopup.stop();
				}
				private Map<String, Boolean> getRetainedStates() {
					Map<String, Boolean> retainedStates = new HashMap<String, Boolean>();
					for(int i=0; i<view.getTree().getItemCount(); i++) {
						FileImageLabelTreeItem fileImageLabelTreeItem = view.getTree().getItem(i);
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
