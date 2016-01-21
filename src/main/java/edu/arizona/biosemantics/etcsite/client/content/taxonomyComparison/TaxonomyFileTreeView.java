package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.DataProxy;

import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.FileTreeView;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileServiceAsync;

public class TaxonomyFileTreeView extends FileTreeView {

	private RpcProxy<FileTreeItem, List<FileTreeItem>> proxy;

	public TaxonomyFileTreeView(IFileServiceAsync fileService) {
		super(fileService, null);
	}

	protected DataProxy<FileTreeItem, List<FileTreeItem>> getProxy() {
		if(proxy == null)
			proxy = new RpcProxy<FileTreeItem, List<FileTreeItem>>() {
				@Override
				public void load(FileTreeItem loadConfig, AsyncCallback<List<FileTreeItem>> callback) {
					if(loadConfig == null || loadConfig instanceof FolderTreeItem) 
						fileService.getTaxonomies(Authentication.getInstance().getToken(), (FolderTreeItem)loadConfig, callback);
				}
			};
		return proxy;
	}
	
}
