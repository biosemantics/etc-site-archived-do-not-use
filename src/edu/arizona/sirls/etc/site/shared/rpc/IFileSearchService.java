package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

@RemoteServiceRelativePath("fileSearch")
public interface IFileSearchService extends RemoteService {

	public RPCResult<List<SearchResult>> search(AuthenticationToken authenticationToken, String filePath, Search search);

	public RPCResult<String> getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search);
	
}
