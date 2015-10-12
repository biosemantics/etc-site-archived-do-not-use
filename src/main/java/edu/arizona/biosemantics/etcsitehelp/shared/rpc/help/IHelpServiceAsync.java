package edu.arizona.biosemantics.etcsitehelp.shared.rpc.help;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;


public interface IHelpServiceAsync {

	public void getHelp(Help help, AsyncCallback<SafeHtml> callback);
	
}
