package edu.arizona.biosemantics.etcsite.etcsitehelpcenter.client.layout;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.help.HelpContent;

public interface IEtcSiteHelpView extends IsWidget {

	public interface Presenter {

		IEtcSiteHelpView getView();
		
		void populateHelpContent(Help hepl);
	}
	
	void setPresenter(Presenter presenter);
	void addContent(JsArray<HelpContent> contents);

}