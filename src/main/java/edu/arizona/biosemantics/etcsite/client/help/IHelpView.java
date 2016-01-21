package edu.arizona.biosemantics.etcsite.client.help;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.help.HelpContent;

public interface IHelpView {

	public interface Presenter {
		IHelpView getView();
	}

	IsWidget asWidget();

	void setPresenter(Presenter presenter);
	void addContent(JsArray<HelpContent> contents);
	
}
