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

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;

public class FileDragDropHandler implements DragStartHandler, DropHandler, DragOverHandler {

	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private Set<IFileMoveListener> listeners = new HashSet<IFileMoveListener>();
		
	@Override
	public void onDragStart(DragStartEvent event) {	
		Object source = event.getSource();
		if(source instanceof FileImageLabel) {
			// Required: set data for the event.
			FileImageLabel fileImageLabel = (FileImageLabel)source;
			event.setData("sourcePath", fileImageLabel.getFileInfo().getFilePath());
			event.setData("sourceName", fileImageLabel.getFileInfo().getName(true));
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
				fileService.isDirectory(Authentication.getInstance().getToken(), sourcePath, new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean isDirectory) {
							//String targetAndAddonPath = fileImageLabel.getFileInfo().getFilePath() + File.seperator + sourceName;
							String targetPath = fileImageLabel.getFileInfo().getFilePath();
							if(!fileImageLabel.getFileInfo().getFileType().equals(FileTypeEnum.DIRECTORY))
								//targetAndAddonPath = getParentDirectory(fileImageLabel.getPath()) + File.seperator + sourceName;
								targetPath = getParent(fileImageLabel.getFileTreeItem());
							final int targetLevel = getLevel(fileImageLabel.getFileTreeItem());
							final String targetPathFinal = targetPath;
							
							if(isDirectory) {
								fileService.getDepth(Authentication.getInstance().getToken(), sourcePath, 
										new AsyncCallback<Integer>() {
									@Override
									public void onSuccess(Integer sourceDepth) {
										int overallDepth = targetLevel + (sourceDepth + 1);
										if(overallDepth > Configuration.fileManagerMaxDepth) {
											Alerter.maxDepthReached();
											return;
										} else {
											moveFile(sourcePath, targetPathFinal);
										}
									}
									@Override
									public void onFailure(Throwable caught) {
										Alerter.failedToGetDepth(caught);
									}
								});
							} else 
								moveFile(sourcePath, targetPathFinal);
						}
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToIsDirectory(caught);
						}
				});
			} else {
				Alerter.invalidMoveToDecendant();
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
				new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				notifyListeners();	
			}
			public void onFailure(Throwable caught) {
				Alerter.failedToMoveFile(caught);
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
