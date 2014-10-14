package edu.arizona.biosemantics.etcsite.shared.rpc.file.search;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.shared.model.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.SearchResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;

public interface IFileSearchServiceAsync {

	public void search(AuthenticationToken authenticationToken, String filePath, Search search, AsyncCallback<List<SearchResult>> callback);

	public void getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search, AsyncCallback<String> callback);
	
}
