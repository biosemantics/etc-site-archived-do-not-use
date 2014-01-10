package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.file.search.SearchResult;

@RemoteServiceRelativePath("fileSearch")
public interface IFileSearchService extends RemoteService {

	public RPCResult<List<SearchResult>> search(AuthenticationToken authenticationToken, String filePath, Search search);

	public RPCResult<String> getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search);
	
}
