package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.fileManager.DirectoryTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeItem;

public class FileTreeDecorator {


	
	public void decorate(Tree tree, edu.arizona.sirls.etc.site.shared.rpc.Tree<String> fileTree, FileDragDropHandler fileDragDropHandler) {
		String path = getPath(fileTree);
		FileImageLabelTreeItem root = new FileTreeItem(fileTree.getValue(), path);		
		if(fileTree.isContainerTree()) {
			root = new DirectoryTreeItem(fileTree.getValue(), path);
		} 
	
		if(fileDragDropHandler != null) {
			FileImageLabelComposite fileComposite = root.getFileImageLabelComposite();
			fileComposite.getElement().setDraggable(Element.DRAGGABLE_TRUE);
			fileComposite.addDomHandler(fileDragDropHandler, DragStartEvent.getType());
			fileComposite.addDomHandler(fileDragDropHandler, DragOverEvent.getType());
			fileComposite.addDomHandler(fileDragDropHandler, DropEvent.getType());
		}
		tree.addItem(root);

		if(fileTree.isContainerTree()) {
			for(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> child : fileTree.getChildren()) {
				decorate(root, child, fileDragDropHandler);
			}
		}
		
		root.setState(true);
	}

	private void decorate(TreeItem root, edu.arizona.sirls.etc.site.shared.rpc.Tree<String> fileTree, FileDragDropHandler fileDragAndDropHandler) {
		String path = getPath(fileTree);
		FileImageLabelTreeItem treeItem = new FileTreeItem(fileTree.getValue(), path);		
		FileImageLabelComposite fileComposite = treeItem.getFileImageLabelComposite();
		FileImageLabelCompositeDoubleClickHandler fileDoubleClickHandler = new FileImageLabelCompositeDoubleClickHandler(fileComposite);
		fileComposite.addDomHandler(fileDoubleClickHandler, DoubleClickEvent.getType());
		
		if(fileTree.isContainerTree()) {
			treeItem = new DirectoryTreeItem(fileTree.getValue(), path);
		} 
		root.addItem(treeItem);
		
		if(fileDragAndDropHandler != null) { 
			fileComposite.getElement().setDraggable(Element.DRAGGABLE_TRUE);
			fileComposite.addDomHandler(fileDragAndDropHandler, DragStartEvent.getType());
			fileComposite.addDomHandler(fileDragAndDropHandler, DragOverEvent.getType());
			fileComposite.addDomHandler(fileDragAndDropHandler, DropEvent.getType());
		}
		
		if(fileTree.isContainerTree()) {
			for(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> child : fileTree.getChildren()) {
				decorate(treeItem, child, fileDragAndDropHandler);
			}
		}
		
		treeItem.setState(true);
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
}
