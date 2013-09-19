package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class FileTreeDecorator {

	private String folderImage = "images//Folder.gif";
	private String fileImage = "images//File.gif";
	
	public void decorate(Tree tree, edu.arizona.sirls.etc.site.shared.rpc.Tree<String> fileTree, FileDragDropHandler fileDragDropHandler) {
		String path = getPath(fileTree);
		FileImageLabelComposite fileComposite = new FileImageLabelComposite(fileImage, "16", "20", fileTree.getValue(), path);
		
		if(fileTree.isContainerTree()) {
			fileComposite = new FileImageLabelComposite(folderImage, "19", "20", fileTree.getValue(), path);
		} 
		
		
		TreeItem root = new TreeItem(fileComposite);
		if(fileDragDropHandler != null) {
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
		FileImageLabelComposite fileComposite = new FileImageLabelComposite(fileImage, "16", "20", fileTree.getValue(), path);
		FileImageLabelCompositeDoubleClickHandler fileDoubleClickHandler = new FileImageLabelCompositeDoubleClickHandler(fileComposite);
		fileComposite.addDomHandler(fileDoubleClickHandler, DoubleClickEvent.getType());
		if(fileTree.isContainerTree()) {
			fileComposite = new FileImageLabelComposite(folderImage, "19", "20", fileTree.getValue(), path);
		}
		
		TreeItem treeItem = new TreeItem(fileComposite);
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
