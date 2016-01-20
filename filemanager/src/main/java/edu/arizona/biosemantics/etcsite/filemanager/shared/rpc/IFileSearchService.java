package edu.arizona.biosemantics.etcsite.filemanager.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.search.Search;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.search.SearchResult;

@RemoteServiceRelativePath("fileSearch")
public interface IFileSearchService extends RemoteService {

	public List<SearchResult> search(AuthenticationToken authenticationToken, String filePath, Search search) throws InvalidSearchException, PermissionDeniedException, GetFileContentFailedException, SearchException;

	public String getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search) throws InvalidSearchException, PermissionDeniedException, GetFileContentFailedException, SearchException;
	
}
