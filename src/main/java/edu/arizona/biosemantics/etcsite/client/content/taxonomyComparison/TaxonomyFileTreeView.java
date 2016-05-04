package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.DataProxy;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FileTreeView;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;

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