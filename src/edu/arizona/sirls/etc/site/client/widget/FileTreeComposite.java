package edu.arizona.sirls.etc.site.client.widget;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileDragDropHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileSelectionHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.IFileMoveListener;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class FileTreeComposite extends Composite implements IFileMoveListener {
	
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private final Tree tree = new Tree();
	private FileSelectionHandler fileSelectionHandler = new FileSelectionHandler();
	private FileDragDropHandler fileDragDropHandler = new FileDragDropHandler();
	private boolean enableDragAndDrop;
	
	public FileTreeComposite(boolean enableDragAndDrop) { 
		this.enableDragAndDrop = enableDragAndDrop;
		tree.addSelectionHandler(fileSelectionHandler);	
		initWidget(tree);
	}
	
	public void refresh() {
		this.fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), userFilesCallback);	
	}

	protected AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> userFilesCallback = 
			new AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>>() {
		public void onSuccess(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result) {
			tree.clear();
			
			FileTreeDecorator fileTreeDecorator = new FileTreeDecorator();
			fileDragDropHandler.addListener(FileTreeComposite.this);
			if(enableDragAndDrop) 
				fileTreeDecorator.decorate(tree, result, fileDragDropHandler);
			else
				fileTreeDecorator.decorate(tree, result, null);
			
			notifyLoadListeners();
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};

	@Override
	public void notifyFileMove() {
		this.refresh();
	}

	public FileSelectionHandler getFileSelectionHandler() {
		return this.fileSelectionHandler;
	}
	
	private Set<ILoadListener> listeners = new HashSet<ILoadListener>();
	
	public void addLoadListener(ILoadListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeLoadListener(ILoadListener listener) { 
		this.listeners.remove(listener);
	}
	
	public void notifyLoadListeners() { 
		for(ILoadListener listener : listeners)
			listener.notifyLoadFinished(this);
	}
}
