package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.TreeItem;

import edu.arizona.sirls.etc.site.client.view.fileManager.DirectoryTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTree;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeItem;
import edu.arizona.sirls.etc.site.shared.rpc.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.Tree;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;

public class FileTreeDecorator {
	
	public void decorate(FileImageLabelTree tree, edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> fileTree, FileFilter fileFilter, 
			FileDragDropHandler fileDragDropHandler, String selectionPath, 
			Map<String, Boolean> retainedStates) {
		FileInfo fileInfo = fileTree.getValue();
		String filePath = fileInfo.getFilePath();
		
		FileImageLabelTreeItem root = new FileTreeItem(fileInfo.getName(), fileInfo);		
		if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
			String contentString = getContentString(fileTree);
			root = new DirectoryTreeItem(fileInfo.getName() + " " + contentString, fileInfo);
		} 
		
		if(fileDragDropHandler != null) {
			FileImageLabelComposite fileComposite = root.getFileImageLabelComposite();
			//fileComposite.addDomHandler(fileDragDropHandler, DragStartEvent.getType());
			fileComposite.addDomHandler(fileDragDropHandler, DragOverEvent.getType());
			fileComposite.addDomHandler(fileDragDropHandler, DropEvent.getType());
			fileComposite.getElement().setDraggable(Element.DRAGGABLE_FALSE);
		}
		
		tree.addItem(root);
		if(selectionPath != null && filePath != null && filePath.equals(selectionPath))
			tree.setSelectedItem(root);
		
		if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
			for(edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> child : fileTree.getChildren()) {
				decorate(tree, root, child, fileFilter, fileDragDropHandler, 1, selectionPath, retainedStates);
			}
		}
		
		if(retainedStates.containsKey(filePath))
			root.setState(retainedStates.get(filePath));
		else
			root.setState(true);
	}
	
	private boolean filter(FileType fileType, FileFilter fileFilter) {
		switch(fileFilter) {
		case TAXON_DESCRIPTION:
			return !fileType.equals(FileType.TAXON_DESCRIPTION);
		case GLOSSARY:
			return !fileType.equals(FileType.GLOSSARY);
		case EULER:
			return !fileType.equals(FileType.EULER);
		case ALL:
			return false;
		case FILE:
			return fileType.equals(FileType.DIRECTORY);
		case DIRECTORY:
			return !fileType.equals(FileType.DIRECTORY);
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
			if(childTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
				numberOfChildDirectories++;
			} else {
				numberOfChildFiles++;
			}
		}
		return "[" + numberOfChildDirectories + " directories, " + numberOfChildFiles + " files]";
	}

	private void decorate(FileImageLabelTree tree, TreeItem root, edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> fileTree, FileFilter fileFilter, 
			FileDragDropHandler fileDragAndDropHandler, 
			int level, String selectionPath, Map<String, Boolean> retainedStates) {
		if(!filter(fileTree.getValue().getFileType(), fileFilter)) {
			FileInfo fileInfo = fileTree.getValue();
			String filePath = fileInfo.getFilePath();
			FileImageLabelTreeItem treeItem = new FileTreeItem(fileInfo.getName(), fileInfo);		
			FileImageLabelComposite fileComposite = treeItem.getFileImageLabelComposite();
			FileImageLabelCompositeDoubleClickHandler fileDoubleClickHandler = new FileImageLabelCompositeDoubleClickHandler(fileComposite);
			fileComposite.addDomHandler(fileDoubleClickHandler, DoubleClickEvent.getType());
			
			if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
				//String name = fileTree.getValue() + " [" + getNumberOfContainers(fileTree.getChildren()) + " folder, " + getNumberOfFiles(fileTree.getChildren()) + " files]";
				String contentString = getContentString(fileTree);
				treeItem = new DirectoryTreeItem(fileInfo.getName() + " " + contentString, fileInfo);
				if(level > Configuration.fileManagerMaxDepth)
					return;
			} 
			root.addItem(treeItem);
			if(selectionPath != null && filePath != null && filePath.equals(selectionPath))
				tree.setSelectedItem(treeItem);
			
			if(fileDragAndDropHandler != null) { 
				fileComposite = treeItem.getFileImageLabelComposite();
				fileComposite.getElement().setDraggable(Element.DRAGGABLE_TRUE);
				fileComposite.addDomHandler(fileDragAndDropHandler, DragStartEvent.getType());
				fileComposite.addDomHandler(fileDragAndDropHandler, DragOverEvent.getType());
				fileComposite.addDomHandler(fileDragAndDropHandler, DropEvent.getType());
			}
			
			if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
				level++;
				for(edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> child : fileTree.getChildren()) {
					decorate(tree, treeItem, child, fileFilter, fileDragAndDropHandler, level, selectionPath, retainedStates);
				}
			}
			
			if(retainedStates.containsKey(filePath))
				treeItem.setState(retainedStates.get(filePath));
		}
	}

}
