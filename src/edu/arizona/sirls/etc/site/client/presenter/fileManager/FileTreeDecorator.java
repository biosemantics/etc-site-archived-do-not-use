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
import edu.arizona.sirls.etc.site.shared.rpc.Tree;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileInfo;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;

public class FileTreeDecorator {
	
	public void decorate(FileImageLabelTree tree, edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> fileTree, FileFilter fileFilter, 
			FileDragDropHandler fileDragDropHandler, String selectionTarget, 
			Map<String, Boolean> retainedStates) {
		String path = getPath(fileTree);

		FileImageLabelTreeItem root = new FileTreeItem(fileTree.getValue().getName(), path);		
		if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
			String name = fileTree.getValue().getName();
			String contentString = getContentString(fileTree);
			root = new DirectoryTreeItem(name + " " + contentString, path);
		} 
	
		if(fileDragDropHandler != null) {
			FileImageLabelComposite fileComposite = root.getFileImageLabelComposite();
			//fileComposite.addDomHandler(fileDragDropHandler, DragStartEvent.getType());
			fileComposite.addDomHandler(fileDragDropHandler, DragOverEvent.getType());
			fileComposite.addDomHandler(fileDragDropHandler, DropEvent.getType());
			fileComposite.getElement().setDraggable(Element.DRAGGABLE_FALSE);
		}
		
		tree.addItem(root);
		if(path.equals(selectionTarget))
			tree.setSelectedItem(root);
		
		if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
			for(edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> child : fileTree.getChildren()) {
				decorate(tree, root, child, fileFilter, fileDragDropHandler, 1, selectionTarget, retainedStates);
			}
		}
		
		if(retainedStates.containsKey(path))
			root.setState(retainedStates.get(path));
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
			int level, String selectionTarget, Map<String, Boolean> retainedStates) {
		if(!filter(fileTree.getValue().getFileType(), fileFilter)) {
			String path = getPath(fileTree);
			FileImageLabelTreeItem treeItem = new FileTreeItem(fileTree.getValue().getName(), path);		
			FileImageLabelComposite fileComposite = treeItem.getFileImageLabelComposite();
			FileImageLabelCompositeDoubleClickHandler fileDoubleClickHandler = new FileImageLabelCompositeDoubleClickHandler(fileComposite);
			fileComposite.addDomHandler(fileDoubleClickHandler, DoubleClickEvent.getType());
			
			if(fileTree.getValue().getFileType().equals(FileType.DIRECTORY)) {
				//String name = fileTree.getValue() + " [" + getNumberOfContainers(fileTree.getChildren()) + " folder, " + getNumberOfFiles(fileTree.getChildren()) + " files]";
				String name = fileTree.getValue().getName();
				String contentString = getContentString(fileTree);
				treeItem = new DirectoryTreeItem(name + " " + contentString, path);
				if(level > 2)
					return;
			} 
			root.addItem(treeItem);
			if(path.equals(selectionTarget))
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
					decorate(tree, treeItem, child, fileFilter, fileDragAndDropHandler, level, selectionTarget, retainedStates);
				}
			}
			
			if(retainedStates.containsKey(path))
				treeItem.setState(retainedStates.get(path));
		}
	}


	private String getPath(edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> fileTree) {
		String result = "";
		edu.arizona.sirls.etc.site.shared.rpc.Tree<FileInfo> currentNode = fileTree;
		do {
			String value = currentNode.getValue().getName();
			if(result.isEmpty())
				result = value;
			else 
				result = value + "//" + result;
			currentNode = currentNode.getParent();
		} while(currentNode != null);
		return result;
	}
}
