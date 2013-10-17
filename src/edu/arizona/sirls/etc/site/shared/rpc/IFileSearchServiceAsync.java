package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public interface IFileSearchServiceAsync {

	public void search(AuthenticationToken authenticationToken, String input, Search search, AsyncCallback<List<SearchResult>> callback);

	public void getFileContentSearched(AuthenticationToken authenticationToken, String target, Search search, AsyncCallback<String> callback);
	
}
