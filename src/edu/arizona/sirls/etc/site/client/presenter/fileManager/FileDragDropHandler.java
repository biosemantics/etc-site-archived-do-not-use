package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.shared.rpc.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileTypeEnum;

public class FileDragDropHandler implements DragStartHandler, DropHandler, DragOverHandler {

	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private Set<IFileMoveListener> listeners = new HashSet<IFileMoveListener>();

	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "File Move");
	
	@Override
	public void onDragStart(DragStartEvent event) {		
		Object source = event.getSource();
		if(source instanceof FileImageLabelComposite) {
			// Required: set data for the event.
			FileImageLabelComposite fileImageLabelComposite = (FileImageLabelComposite)source;
			event.setData("sourcePath", fileImageLabelComposite.getFileInfo().getFilePath());
			event.setData("sourceName", fileImageLabelComposite.getFileInfo().getName());
			event.setData("fileType", fileImageLabelComposite.getFileInfo().getFileType().toString());
		}
	}

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();
		final String sourcePath = event.getData("sourcePath");
		final String sourceName = event.getData("sourceName");
		Object target = event.getSource();
		if(target instanceof FileImageLabelComposite) {
			// Required: set data for the event.
			final FileImageLabelComposite fileImageLabelComposite = (FileImageLabelComposite)target;
			String targetPath = fileImageLabelComposite.getFileInfo().getFilePath();
			
			if(!targetPath.contains(sourcePath)) {
				fileService.isDirectory(Authentication.getInstance().getAuthenticationToken(), 
						sourcePath, new AsyncCallback<RPCResult<Boolean>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(RPCResult<Boolean> isDirectory) {
						if(isDirectory.isSucceeded()) {
							//String targetAndAddonPath = fileImageLabelComposite.getFileInfo().getFilePath() + File.seperator + sourceName;
							String targetPath = fileImageLabelComposite.getFileInfo().getFilePath();
							if(!fileImageLabelComposite.getFileInfo().getFileType().equals(FileTypeEnum.DIRECTORY))
								//targetAndAddonPath = getParentDirectory(fileImageLabelComposite.getPath()) + File.seperator + sourceName;
								targetPath = getParent(fileImageLabelComposite.getFileTreeItem());
							final int targetLevel = getLevel(fileImageLabelComposite.getFileTreeItem());
							final String targetPathFinal = targetPath;
							
							if(isDirectory.getData()) {
								fileService.getDepth(Authentication.getInstance().getAuthenticationToken(), sourcePath, 
										new AsyncCallback<RPCResult<Integer>>() {
									
									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
									@Override
									public void onSuccess(RPCResult<Integer> sourceDepth) {
										if(sourceDepth.isSucceeded()) {
											int overallDepth = targetLevel + (sourceDepth.getData() + 1);
											if(overallDepth > Configuration.fileManagerMaxDepth) {
												messagePresenter.setMessage("Only a directory depth of " + Configuration.fileManagerMaxDepth + " is allowed.");
												messagePresenter.go();
												return;
											} else {
												moveFile(sourcePath, targetPathFinal);
											}
										}
									}
								});
							} else {
								moveFile(sourcePath, targetPathFinal);
							}
						}
					}
				});
			} else {
				messagePresenter.setMessage("Directory cannot be moved into its descendants.");
				messagePresenter.go();
			}
		}
	}
	
	private String getParent(FileImageLabelTreeItem selection) {
		TreeItem parentItem = selection.getParentItem();
		if(parentItem != null && parentItem instanceof FileImageLabelTreeItem) {
			FileImageLabelTreeItem parentFileTreeItem = (FileImageLabelTreeItem)parentItem;
			if(parentFileTreeItem.getFileInfo().getFilePath() == null) {
				return this.getParent(parentFileTreeItem);
			}
			return parentFileTreeItem.getFileInfo().getFilePath();
		}
		return null;
	}
	
	private int getLevel(TreeItem item) {
		int result = 0;
		while(item.getParentItem() != null) {
			result++;
			item = item.getParentItem();
		}
		return result;
	}
	
	protected void moveFile(String sourcePath, String targetPath) {
		fileService.moveFile(Authentication.getInstance().getAuthenticationToken(), sourcePath, targetPath, 
				new AsyncCallback<RPCResult<Void>>() {
			public void onSuccess(RPCResult<Void> result) {
				if(result.isSucceeded()) {
					notifyListeners();
				}	
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		//this is required for drop to work, see 
		// http://static.googleusercontent.com/external_content/untrusted_dlcp/www.google.com/it//events/io/2011/static/presofiles/gwt_html5_a_web_develops_dream.pdf slide 62
	}
	
	public void addListener(IFileMoveListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IFileMoveListener listener) { 
		this.listeners.remove(listener);
	}
	
	public void notifyListeners() { 
		for(IFileMoveListener listener : listeners)
			listener.notifyFileMove();
	}	
}
