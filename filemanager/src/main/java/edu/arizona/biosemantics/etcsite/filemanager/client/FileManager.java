package edu.arizona.biosemantics.etcsite.filemanager.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AdminAuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileServiceAsync;

public class FileManager implements EntryPoint {
		
	@Override
	public void onModuleLoad() {	
		final IFileServiceAsync rpcService = GWT
				.create(IFileService.class);

		RpcProxy<FileTreeItem, List<FileTreeItem>> proxy = new RpcProxy<FileTreeItem, List<FileTreeItem>>() {
			@Override
			public void load(FileTreeItem loadConfig,
					AsyncCallback<List<FileTreeItem>> callback) {
				if(loadConfig == null || loadConfig instanceof FolderTreeItem) 
					rpcService.getFiles(new AdminAuthenticationToken(), (FolderTreeItem)loadConfig, null, callback);
			}
		};

		TreeLoader<FileTreeItem> loader = new TreeLoader<FileTreeItem>(proxy) {
			@Override
			public boolean hasChildren(FileTreeItem parent) {
				return parent instanceof FolderTreeItem;
			}
		};

		TreeStore<FileTreeItem> store = new TreeStore<FileTreeItem>(
				new ModelKeyProvider<FileTreeItem>() {
					@Override
					public String getKey(FileTreeItem item) {
						return item.getName(true);
					}
				});

		loader.addLoadHandler(new ChildTreeStoreBinding<FileTreeItem>(store));

		final Tree<FileTreeItem, String> tree = new Tree<FileTreeItem, String>(store,
				new ValueProvider<FileTreeItem, String>() {
					@Override
					public String getValue(FileTreeItem object) {
						return object.getName(true);
					}

					@Override
					public void setValue(FileTreeItem object, String value) {
					}

					@Override
					public String getPath() {
						return "value";
					}
				});

		tree.setLoader(loader);
		final FolderImages folderImages = GWT.create(FolderImages.class);
		tree.setIconProvider(new IconProvider<FileTreeItem>() {
			@Override
			public ImageResource getIcon(FileTreeItem model) {
				if(model instanceof FolderTreeItem)
					return folderImages.green();
				return folderImages.orange();
			}
		});

		tree.setWidth(300);

		RootLayoutPanel.get().add(tree);
	}
}
