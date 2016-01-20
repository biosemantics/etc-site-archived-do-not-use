package edu.arizona.biosemantics.etcsitehelp.shared.rpc.help;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;

@RemoteServiceRelativePath("../help")
public interface IHelpService extends RemoteService {
	
	public String getHelp(Help help) throws Exception;
	
}
