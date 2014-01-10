package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;

public interface IFileTreeView extends IsWidget {
	
	public interface Presenter extends IFileTreeSelectionListener {
		FileImageLabelTreeItem getSelectedItem();
		void clearSelection();
		void refresh(FileFilter fileFilter);
		void addSelectionListener(IFileTreeSelectionListener listener);
		IFileTreeView getView();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void clear();
	int getItemCount();
	FileImageLabelTreeItem getItem(int i);
	void addItem(FileImageLabelTreeItem root);
	void setSelectedItem(FileImageLabelTreeItem root);
	
	public interface IFileTreeSelectionListener {
		void onSelect(FileImageLabelTreeItem selectedItem);
	}
}
