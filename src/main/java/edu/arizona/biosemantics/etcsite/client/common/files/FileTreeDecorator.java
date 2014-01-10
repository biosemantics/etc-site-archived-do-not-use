package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Configuration;
import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.Tree;

public class FileTreeDecorator {
	
	private IFileAccessServiceAsync fileAccessService;
	private IFileContentView.Presenter fileContentPresenter;

	@Inject
	public FileTreeDecorator(IFileAccessServiceAsync fileAccessService, IFileContentView.Presenter fileContentPresenter) {
		this.fileAccessService = fileAccessService;
		this.fileContentPresenter = fileContentPresenter;
	}
	
	public Map<String, FileImageLabelTreeItem> decorate(IFileTreeView view, edu.arizona.biosemantics.etcsite.shared.rpc.Tree<FileInfo> fileTree, FileFilter fileFilter, 
			FileDragDropHandler fileDragDropHandler, String selectionPath, 
			Map<String, Boolean> retainedStates) {
		Map<String, FileImageLabelTreeItem> result = new HashMap<String, FileImageLabelTreeItem>();
		FileInfo fileInfo = fileTree.getValue();
		String filePath = fileInfo.getFilePath();
		
		FileImageLabelTreeItem root = new FileTreeItem(fileInfo.getName(), fileInfo);		
		if(fileTree.getValue().getFileType().equals(FileTypeEnum.DIRECTORY)) {
			String contentString = getContentString(fileTree);
			root = new DirectoryTreeItem(fileInfo.getName() + " " + contentString, fileInfo);
		} 
		
		//if(fileDragDropHandler != null) {
		//	FileImageLabel fileComposite = root.getFileImageLabel();
			//fileComposite.addDomHandler(fileDragDropHandler, DragStartEvent.getType());
			//fileComposite.addDomHandler(fileDragDropHandler, DragOverEvent.getType());
			//fileComposite.addDomHandler(fileDragDropHandler, DropEvent.getType());
			//fileComposite.getElement().setDraggable(Element.DRAGGABLE_FALSE);
		//}
		
		view.addItem(root);
		if(selectionPath != null && filePath != null && filePath.equals(selectionPath))
			view.setSelectedItem(root);
		
		if(fileTree.getValue().getFileType().equals(FileTypeEnum.DIRECTORY)) {
			for(edu.arizona.biosemantics.etcsite.shared.rpc.Tree<FileInfo> child : fileTree.getChildren()) {
				decorate(view, root, child, fileFilter, fileDragDropHandler, 1, selectionPath, retainedStates, result);
			}
		}
		
		if(retainedStates.containsKey(filePath))
			root.setState(retainedStates.get(filePath));
		else
			root.setState(true);
		return result;
	}
	
	private boolean filter(FileTypeEnum fileType, FileFilter fileFilter) {
		switch(fileFilter) {
		case TAXON_DESCRIPTION:
			return !fileType.equals(FileTypeEnum.TAXON_DESCRIPTION);
		case MARKED_UP_TAXON_DESCRIPTION:
			return !fileType.equals(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION);
		case MATRIX:
			return !fileType.equals(FileTypeEnum.MATRIX);
		case ALL:
			return false;
		case FILE:
			return fileType.equals(FileTypeEnum.DIRECTORY);
		case DIRECTORY:
			return !fileType.equals(FileTypeEnum.DIRECTORY);
		}
		return true;
	}

	private String getContentString(Tree<FileInfo> fileTree) {
		int numberOfChildFiles = 0;
		int numberOfChildDirectories = 0;
		for(Tree<FileInfo> childTree : fileTree.getChildren()) {
			//System.out.println(childTree);
			//System.out.println(childTree.getValue());
			//System.out.println(childTree.getValue().getFileType());
			if(childTree.getValue().getFileType().equals(FileTypeEnum.DIRECTORY)) {
				numberOfChildDirectories++;
			} else {
				numberOfChildFiles++;
			}
		}
		return "[" + numberOfChildDirectories + " directories, " + numberOfChildFiles + " files]";
	}

	private void decorate(IFileTreeView view, TreeItem root, edu.arizona.biosemantics.etcsite.shared.rpc.Tree<FileInfo> fileTree, FileFilter fileFilter, 
			FileDragDropHandler fileDragAndDropHandler, 
			int level, String selectionPath, Map<String, Boolean> retainedStates, Map<String, FileImageLabelTreeItem> filePathTreeItemMap) {
		if(!filter(fileTree.getValue().getFileType(), fileFilter)) {
			FileInfo fileInfo = fileTree.getValue();
			String filePath = fileInfo.getFilePath();
			FileImageLabelTreeItem treeItem = new FileTreeItem(fileInfo.getName(), fileInfo);		
			
			if(fileTree.getValue().getFileType().equals(FileTypeEnum.DIRECTORY)) {
				//String name = fileTree.getValue() + " [" + getNumberOfContainers(fileTree.getChildren()) + " folder, " + getNumberOfFiles(fileTree.getChildren()) + " files]";
				String contentString = getContentString(fileTree);
				treeItem = new DirectoryTreeItem(fileInfo.getName() + " " + contentString, fileInfo);
				
				if(level > Configuration.fileManagerMaxDepth)
					return;
			} 
			root.addItem(treeItem);
			filePathTreeItemMap.put(treeItem.getFileInfo().getFilePath(), treeItem);
			if(selectionPath != null && filePath != null && filePath.equals(selectionPath))
				view.setSelectedItem(treeItem);
			
			final FileImageLabel fileComposite = treeItem.getFileImageLabel();
			if(!fileTree.getValue().getFileType().equals(FileTypeEnum.DIRECTORY)) {
				fileComposite.addDomHandler(new DoubleClickHandler() {
					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						fileContentPresenter.show(fileComposite.getFileInfo().getFilePath()); 
					}
				}, DoubleClickEvent.getType());
			}
			
			if(fileDragAndDropHandler != null) { 
				fileComposite.getElement().setDraggable(Element.DRAGGABLE_TRUE);
				fileComposite.addDomHandler(fileDragAndDropHandler, DragStartEvent.getType());
				fileComposite.addDomHandler(fileDragAndDropHandler, DragOverEvent.getType());
				fileComposite.addDomHandler(fileDragAndDropHandler, DropEvent.getType());
			}
			
			if(fileTree.getValue().getFileType().equals(FileTypeEnum.DIRECTORY)) {
				level++;
				for(edu.arizona.biosemantics.etcsite.shared.rpc.Tree<FileInfo> child : fileTree.getChildren()) {
					decorate(view, treeItem, child, fileFilter, fileDragAndDropHandler, level, selectionPath, retainedStates, filePathTreeItemMap);
				}
			}
			
			if(retainedStates.containsKey(filePath)) {
				treeItem.setState(retainedStates.get(filePath));
			} else {
				treeItem.setState(false);
			}
		}
	}

}
