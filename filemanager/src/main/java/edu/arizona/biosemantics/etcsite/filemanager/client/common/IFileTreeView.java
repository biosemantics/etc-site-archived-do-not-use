package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;

public interface IFileTreeView extends IsWidget {
		
	public interface Presenter {
		IFileTreeView getView();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
	void setSelection(List<FileTreeItem> selection);
	List<FileTreeItem> getSelection();
	void setSelectionMode(SelectionMode selectionMode);
	void addSelectionChangeHandler(SelectionChangedHandler<FileTreeItem> handler);
	
	FileTreeItem getParent(FileTreeItem fileTreeItem);
	int getDepth(FileTreeItem fileTreeItem);
	
	void refresh(FileFilter fileFilter);
	void refreshChildren(FileTreeItem fileTreeItem, FileFilter fileFilter);
	//void refresh(FileTreeItem selection, FileFilter fileFilter);

}
