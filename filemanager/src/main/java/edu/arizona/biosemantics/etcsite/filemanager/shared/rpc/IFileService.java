package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.filemanager.shared.model.Model;

@RemoteServiceRelativePath("fileService")
public interface IFileService extends RemoteService{
		
	public List<Model> getFolders(Model loadConfig);
	
}
