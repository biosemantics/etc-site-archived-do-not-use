package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.IFileTreeSelectionListener;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import edu.arizona.biosemantics.etcsite.shared.model.file.Tree;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;

public class FileTreePresenter implements IFileTreeView.Presenter {
	
	private IFileServiceAsync fileService;
	protected IFileTreeView view;
	
	protected FileTreeDecorator fileTreeDecorator;
	private FileImageLabelTreeItem selectedItem;
	private Set<IFileTreeSelectionListener> selectionListeners = new HashSet<IFileTreeSelectionListener>();
	
	@Inject
	public FileTreePresenter(IFileTreeView view, IFileServiceAsync fileService, FileTreeDecorator
			fileTreeDecorator) {
		this.view = view;
		view.setPresenter(this);
		this.fileService = fileService;
		this.fileTreeDecorator = fileTreeDecorator;
	}

	public void refresh(final FileFilter fileFilter) {
		//retain "open" state of folders
		final Map<String, Boolean> retainedStates = getRetainedStates();
		
		//retain selection
		final String selectionPath = selectedItem == null? null : selectedItem.getFileInfo().getFilePath();
		
		//remove previous data
		view.clear();
		
		final MessageBox box = Alerter.startLoading();
		this.fileService.getUsersFiles(Authentication.getInstance().getToken(), FileFilter.ALL, 
				new AsyncCallback<Tree<FileInfo>>() {
				@Override
				public void onSuccess(Tree<FileInfo> result) {
					decorate(result, selectionPath, retainedStates, fileFilter);
					Alerter.stopLoading(box);
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToGetUsersFiles(caught);
					Alerter.stopLoading(box);
				}
			}
		);
	}
	
	protected void decorate(Tree<FileInfo> tree, String selectionPath, Map<String, Boolean> retainedStates, FileFilter fileFilter) {
		fileTreeDecorator.decorate(view, tree, fileFilter, null, selectionPath, retainedStates);
	}
	
	private Map<String, Boolean> getRetainedStates() {
		Map<String, Boolean> retainedStates = new HashMap<String, Boolean>();
		for(int i=0; i<view.getItemCount(); i++) {
			FileImageLabelTreeItem fileImageLabelTreeItem = view.getItem(i);
			fillChildrenStates(fileImageLabelTreeItem, retainedStates);
		}
		return retainedStates;
	}
	
	private void fillChildrenStates(FileImageLabelTreeItem fileImageLabelTreeItem, Map<String, Boolean> retainedStates) {
		if(fileImageLabelTreeItem.getFileInfo().getFilePath() != null)
			retainedStates.put(fileImageLabelTreeItem.getFileInfo().getFilePath(), fileImageLabelTreeItem.getState());
		for(int i=0; i < fileImageLabelTreeItem.getChildCount(); i++) {
			FileImageLabelTreeItem child = fileImageLabelTreeItem.getChild(i);
			fillChildrenStates(child, retainedStates);
		}
	}

	@Override
	public void onSelect(FileImageLabelTreeItem selectedItem) {
		this.selectedItem = selectedItem;
		setInputFileMultiple();
		this.notifySelectionListeners();
		
	}

	public static native void setInputFileMultiple() /*-{
	var inputs, index;
    inputs = $doc.getElementsByTagName("input");	
    //alert("get "+inputs.length+" inputs");
	for (index = 0; index < inputs.length; ++index) {
		if(inputs[index].getAttribute("type") == "file" && inputs[index].getAttribute("class") == "gwt-FileUpload") {
			inputs[index].setAttribute("multiple", "multiple");
			//alert("set multiple in setInputFileMultiple");
		}
	}
}-*/;
	private void notifySelectionListeners() {
		for(IFileTreeSelectionListener listener : selectionListeners) 
			listener.onSelect(selectedItem);
	}

	@Override
	public FileImageLabelTreeItem getSelectedItem() {
		return selectedItem;
	}

	@Override
	public void clearSelection() {
		view.setSelectedItem(null);
	}

	@Override
	public void addSelectionListener(IFileTreeSelectionListener listener) {
		this.selectionListeners .add(listener);
	}

	@Override
	public IFileTreeView getView() {
		return view;
	}
	
}
