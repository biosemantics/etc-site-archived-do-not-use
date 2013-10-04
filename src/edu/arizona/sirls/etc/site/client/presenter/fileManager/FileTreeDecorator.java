package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.List;
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

public class FileTreeDecorator {
	
	public void decorate(FileImageLabelTree tree, edu.arizona.sirls.etc.site.shared.rpc.Tree<String> fileTree, FileDragDropHandler fileDragDropHandler, String selectionTarget, 
			Map<String, Boolean> retainedStates) {
		String path = getPath(fileTree);

		FileImageLabelTreeItem root = new FileTreeItem(fileTree.getValue(), path);		
		if(fileTree.isContainerTree()) {
			//String name = fileTree.getValue() + " [" + getNumberOfContainers(fileTree.getChildren()) + " folder, " + getNumberOfFiles(fileTree.getChildren()) + " files]";
			String name = fileTree.getValue();
			root = new DirectoryTreeItem(name, path);
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
		
		if(fileTree.isContainerTree()) {
			for(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> child : fileTree.getChildren()) {
				decorate(tree, root, child, fileDragDropHandler, 1, selectionTarget, retainedStates);
			}
		}
		
		if(retainedStates.containsKey(path))
			root.setState(retainedStates.get(path));
		else
			root.setState(true);
	}

	private void decorate(FileImageLabelTree tree, TreeItem root, edu.arizona.sirls.etc.site.shared.rpc.Tree<String> fileTree, FileDragDropHandler fileDragAndDropHandler, 
			int level, String selectionTarget, Map<String, Boolean> retainedStates) {
		String path = getPath(fileTree);
		FileImageLabelTreeItem treeItem = new FileTreeItem(fileTree.getValue(), path);		
		FileImageLabelComposite fileComposite = treeItem.getFileImageLabelComposite();
		FileImageLabelCompositeDoubleClickHandler fileDoubleClickHandler = new FileImageLabelCompositeDoubleClickHandler(fileComposite);
		fileComposite.addDomHandler(fileDoubleClickHandler, DoubleClickEvent.getType());
		
		if(fileTree.isContainerTree()) {
			//String name = fileTree.getValue() + " [" + getNumberOfContainers(fileTree.getChildren()) + " folder, " + getNumberOfFiles(fileTree.getChildren()) + " files]";
			String name = fileTree.getValue();
			treeItem = new DirectoryTreeItem(name, path);
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
		
		if(fileTree.isContainerTree()) {
			level++;
			for(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> child : fileTree.getChildren()) {
				decorate(tree, treeItem, child, fileDragAndDropHandler, level, selectionTarget, retainedStates);
			}
		}
		
		if(retainedStates.containsKey(path))
			treeItem.setState(retainedStates.get(path));
	}


	private String getPath(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> fileTree) {
		String result = "";
		edu.arizona.sirls.etc.site.shared.rpc.Tree<String> currentNode = fileTree;
		do {
			String value = currentNode.getValue();
			if(result.isEmpty())
				result = value;
			else 
				result = value + "//" + result;
			currentNode = currentNode.getParent();
		} while(currentNode != null);
		return result;
	}
	

	private int getNumberOfFiles(List<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> children) {
		int i=0;
		for(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> child : children) {
			if(!child.isContainerTree())
				i++;
		}
		return i;
	}

	private int getNumberOfContainers(List<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> children) {
		int i=0;
		for(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> child : children) {
			if(child.isContainerTree())
				i++;
		}
		return i;
	}
}
