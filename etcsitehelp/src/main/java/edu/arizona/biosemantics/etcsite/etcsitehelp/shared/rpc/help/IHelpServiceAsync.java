package edu.arizona.biosemantics.etcsite.etcsitehelp.shared.rpc.help;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.help.Help;


public interface IHelpServiceAsync {

	public void getHelp(Help help, AsyncCallback<String> callback);
	
}
