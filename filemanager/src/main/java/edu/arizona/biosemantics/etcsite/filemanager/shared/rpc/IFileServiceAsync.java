package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.filemanager.shared.model.Model;

public interface IFileServiceAsync{

	public void getFolders(Model loadConfig, AsyncCallback<List<Model>> callback);

}
