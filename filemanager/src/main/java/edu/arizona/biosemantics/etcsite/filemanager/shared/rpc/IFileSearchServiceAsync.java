package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.search.Search;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.search.SearchResult;

public interface IFileSearchServiceAsync {

	public void search(AuthenticationToken authenticationToken, String filePath, Search search, AsyncCallback<List<SearchResult>> callback);

	public void getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search, AsyncCallback<String> callback);
	
}
