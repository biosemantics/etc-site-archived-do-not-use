package edu.arizona.biosemantics.etcsite.client.common.files;

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
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.Configuration;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class FileDragDropHandler implements DragStartHandler, DropHandler, DragOverHandler {

	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private Set<IFileMoveListener> listeners = new HashSet<IFileMoveListener>();
	private MessagePresenter messagePresenter;
	
	@Inject
	public FileDragDropHandler(MessagePresenter messagePresenter) {
		this.messagePresenter = messagePresenter;
	}
	
	@Override
	public void onDragStart(DragStartEvent event) {	
		Object source = event.getSource();
		if(source instanceof FileImageLabel) {
			// Required: set data for the event.
			FileImageLabel fileImageLabel = (FileImageLabel)source;
			event.setData("sourcePath", fileImageLabel.getFileInfo().getFilePath());
			event.setData("sourceName", fileImageLabel.getFileInfo().getName());
			event.setData("fileType", fileImageLabel.getFileInfo().getFileType().toString());
		}
	}

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();
		final String sourcePath = event.getData("sourcePath");
		final String sourceName = event.getData("sourceName");
		Object target = event.getSource();
		if(target instanceof FileImageLabel) {
			// Required: set data for the event.
			final FileImageLabel fileImageLabel = (FileImageLabel)target;
			String targetPath = fileImageLabel.getFileInfo().getFilePath();
			
			if(!targetPath.contains(sourcePath)) {
				fileService.isDirectory(Authentication.getInstance().getToken(), sourcePath, new RPCCallback<Boolean>() {
						@Override
						public void onResult(Boolean isDirectory) {
							//String targetAndAddonPath = fileImageLabel.getFileInfo().getFilePath() + File.seperator + sourceName;
							String targetPath = fileImageLabel.getFileInfo().getFilePath();
							if(!fileImageLabel.getFileInfo().getFileType().equals(FileTypeEnum.DIRECTORY))
								//targetAndAddonPath = getParentDirectory(fileImageLabel.getPath()) + File.seperator + sourceName;
								targetPath = getParent(fileImageLabel.getFileTreeItem());
							final int targetLevel = getLevel(fileImageLabel.getFileTreeItem());
							final String targetPathFinal = targetPath;
							
							if(isDirectory) {
								fileService.getDepth(Authentication.getInstance().getToken(), sourcePath, 
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
												messagePresenter.showMessage("File Manager", "Only a directory depth of " + Configuration.fileManagerMaxDepth + " is allowed.");
												return;
											} else {
												moveFile(sourcePath, targetPathFinal);
											}
										}
									}
								});
							} else 
								moveFile(sourcePath, targetPathFinal);
						}
				});
			} else {
				messagePresenter.showMessage("File Manager", "Directory cannot be moved into its descendants.");
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
		fileService.moveFile(Authentication.getInstance().getToken(), sourcePath, targetPath, 
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
	
	public interface IFileMoveListener {

		void notifyFileMove();

	}
}
