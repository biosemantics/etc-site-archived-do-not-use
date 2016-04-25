package edu.arizona.biosemantics.etcsitehelpcenter.client.layout;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;

public interface IEtcSiteHelpView extends IsWidget {

	public interface Presenter {

		IEtcSiteHelpView getView();
		
		void populateHelpContent(Help help);
	}
	
	void setPresenter(Presenter presenter);
	void addContent(JsArray<HelpContent> contents);

}