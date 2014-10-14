package edu.arizona.biosemantics.etcsite.shared.rpc.file.search;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsite.shared.model.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.SearchResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;

@RemoteServiceRelativePath("fileSearch")
public interface IFileSearchService extends RemoteService {

	public List<SearchResult> search(AuthenticationToken authenticationToken, String filePath, Search search) throws InvalidSearchException, PermissionDeniedException, GetFileContentFailedException, SearchException;

	public String getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search) throws InvalidSearchException, PermissionDeniedException, GetFileContentFailedException, SearchException;
	
}
