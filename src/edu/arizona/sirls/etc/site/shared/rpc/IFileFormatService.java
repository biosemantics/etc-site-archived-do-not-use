package edu.arizona.sirls.etc.site.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;

@RemoteServiceRelativePath("fileFormat")
public interface IFileFormatService extends RemoteService {

	public boolean isValidTaxonDescription(AuthenticationToken authenticationToken, String target);
	
	public boolean isValidGlossary(AuthenticationToken authenticationToken, String target);

	public boolean isValidEuler(AuthenticationToken authenticationToken, String target);
}
