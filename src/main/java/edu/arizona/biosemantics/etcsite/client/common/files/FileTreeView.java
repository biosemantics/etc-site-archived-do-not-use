package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.state.client.TreeStateHandler;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;

public class FileTreeView extends Composite implements IFileTreeView {

	protected VerticalLayoutContainer container;

	protected Presenter presenter;
	protected final IFileServiceAsync fileService;
 
	protected Tree<FileTreeItem, String> tree;

	protected FileFilter fileFilter;

	protected TreeLoader<FileTreeItem> loader;

	protected TreeStore<FileTreeItem> store;

	protected TreeStateHandler<FileTreeItem> stateHandler;

	protected DataProxy<FileTreeItem, List<FileTreeItem>> proxy;

	@Inject
	public FileTreeView(final IFileServiceAsync fileService, final IFileContentView.Presenter fileContentPresenter) {
		this.fileService = fileService;
		store = new TreeStore<FileTreeItem>(
				new ModelKeyProvider<FileTreeItem>() {
					@Override
					public String getKey(FileTreeItem item) {
						return item.getId();
					}
				});
		tree = new Tree<FileTreeItem, String>(
				store, new ValueProvider<FileTreeItem, String>() {
					@Override
					public String getValue(FileTreeItem object) {
						return object.getText();
					}

					@Override
					public void setValue(FileTreeItem object,
							String value) {
					}

					@Override
					public String getPath() {
						return "name";
					}
				}) {
			 @Override
			    protected void onDoubleClick(Event event) {
			        TreeNode<FileTreeItem> node = findNode(event.getEventTarget().<Element> cast());
			        if(fileContentPresenter != null)
			        	fileContentPresenter.show(node.getModel());
			        super.onDoubleClick(event);
			   }
		};
		loader = new TreeLoader<FileTreeItem>(getProxy()) {
			@Override
			public boolean hasChildren(FileTreeItem parent) {
				return parent instanceof FolderTreeItem;
			}
		};
		loader.addLoadHandler(new LoadHandler<FileTreeItem, List<FileTreeItem>>() {
			@Override
			public void onLoad(LoadEvent<FileTreeItem, List<FileTreeItem>> event) {
				FileTreeItem parent = event.getLoadConfig();
				store.replaceChildren(parent, event.getLoadResult());
			}
		});
		
		tree.setStateful(true);
		tree.setLoader(loader);
		stateHandler = new TreeStateHandler<FileTreeItem>(tree);
		stateHandler.loadState();
		
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		container = new VerticalLayoutContainer();
		container.setHeight(400);
		container.add(tree);
		container.getScrollSupport().setScrollMode(ScrollMode.AUTOY);
	}

	protected DataProxy<FileTreeItem, List<FileTreeItem>> getProxy() {
		if(proxy == null)
			proxy = new RpcProxy<FileTreeItem, List<FileTreeItem>>() {
				@Override
				public void load(FileTreeItem loadConfig, AsyncCallback<List<FileTreeItem>> callback) {
					if(loadConfig == null || loadConfig instanceof FolderTreeItem) 
						fileService.getFiles(Authentication.getInstance().getToken(), (FolderTreeItem)loadConfig, fileFilter, callback);
				}
			};
		return proxy;
	}

	@Override
	public Widget asWidget() {
		return container.asWidget();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setSelection(List<FileTreeItem> selection) {
		tree.getSelectionModel().setSelection(selection);
	}
	
	public int getDepth(FileTreeItem fileTreeItem) {
		return tree.getStore().getDepth(fileTreeItem);
	}

	@Override
	public List<FileTreeItem> getSelection() {
		return tree.getSelectionModel().getSelectedItems();
	}

	@Override
	public void setSelectionMode(SelectionMode selectionMode) {
		tree.getSelectionModel().setSelectionMode(selectionMode);
	}
	
	@Override
	public void addSelectionChangeHandler(SelectionChangedHandler<FileTreeItem> handler) {
		tree.getSelectionModel().addSelectionChangedHandler(handler);
	}
	
	@Override
	public void refresh(FileFilter fileFilter) {
		stateHandler.saveState();
		this.fileFilter = fileFilter;
		loader.load(null);
		stateHandler.loadState();
	}

	@Override
	public void refreshChildren(FileTreeItem fileTreeItem, FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		
		loader.loadChildren(fileTreeItem);
	}
	
	@Override
	public FileTreeItem getParent(FileTreeItem fileTreeItem) {
		return tree.getStore().getParent(fileTreeItem);
	}


	
}