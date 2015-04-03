package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class FileTreeView extends Composite implements IFileTreeView {

	private static FileTreeViewUiBinder uiBinder = GWT.create(FileTreeViewUiBinder.class);

	interface FileTreeViewUiBinder extends UiBinder<Widget, FileTreeView> {
	}

	@UiField
	FileImageLabelTree fileTree;
	
	private Presenter presenter;
	
	public FileTreeView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("fileTree")
	public void onSelection(SelectionEvent<TreeItem> event) {
		if(event.getSelectedItem() instanceof FileImageLabelTreeItem)
			presenter.onSelect((FileImageLabelTreeItem)event.getSelectedItem());
	}
	

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		this.fileTree.clear();
	}

	@Override
	public int getItemCount() {
		return this.fileTree.getItemCount();
	}

	@Override
	public FileImageLabelTreeItem getItem(int i) {
		return this.fileTree.getItem(i);
	}

	@Override
	public void addItem(FileImageLabelTreeItem item) {
		this.fileTree.addItem(item);
	}

	@Override
	public void setSelectedItem(FileImageLabelTreeItem item) {
		this.fileTree.setSelectedItem(item);
	}
		
}
