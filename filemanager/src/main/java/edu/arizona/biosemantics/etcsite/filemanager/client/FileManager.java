package edu.arizona.biosemantics.etcsite.filemanager.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderModel;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.Model;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileServiceAsync;

public class FileManager implements EntryPoint {
		
	@Override
	public void onModuleLoad() {	
		final IFileServiceAsync rpcService = GWT
				.create(IFileService.class);

		RpcProxy<Model, List<Model>> proxy = new RpcProxy<Model, List<Model>>() {
			@Override
			public void load(Model loadConfig,
					AsyncCallback<List<Model>> callback) {
				rpcService.getFolders(loadConfig, callback);
			}
		};

		TreeLoader<Model> loader = new TreeLoader<Model>(proxy) {
			@Override
			public boolean hasChildren(Model parent) {
				return parent instanceof FolderModel;
			}
		};

		TreeStore<Model> store = new TreeStore<Model>(
				new ModelKeyProvider<Model>() {
					@Override
					public String getKey(Model item) {
						return item.value;
					}
				});

		loader.addLoadHandler(new ChildTreeStoreBinding<Model>(store));

		final Tree<Model, String> tree = new Tree<Model, String>(store,
				new ValueProvider<Model, String>() {
					@Override
					public String getValue(Model object) {
						return object.value;
					}

					@Override
					public void setValue(Model object, String value) {
					}

					@Override
					public String getPath() {
						return "value";
					}
				});

		tree.setLoader(loader);
		final FolderImages folderImages = GWT.create(FolderImages.class);
		tree.setIconProvider(new IconProvider<Model>() {
			@Override
			public ImageResource getIcon(Model model) {
				if(model instanceof FolderModel)
					return folderImages.green();
				return folderImages.orange();
			}
		});

		tree.setWidth(300);

		RootLayoutPanel.get().add(tree);
	}
}
