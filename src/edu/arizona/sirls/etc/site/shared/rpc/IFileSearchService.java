package edu.arizona.sirls.etc.site.shared.rpc;

import java.util.List;

import org.w3c.dom.NodeList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

@RemoteServiceRelativePath("fileSearch")
public interface IFileSearchService extends RemoteService {

	public List<SearchResult> search(AuthenticationToken authenticationToken, String input, Search search);

	public String getFileContentSearched(AuthenticationToken authenticationToken, String target, Search search);
	
}
