package edu.arizona.biosemantics.etcsite.client.help;

import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;

public interface IHelpView extends RequiresResize, ProvidesResize {

	public interface Presenter {
		IHelpView getView();
	}

	IsWidget asWidget();

	void setPresenter(Presenter presenter);
	void setContent(JsArray<HelpContent> contents);

	void onHide();
	void onShow();
	
}
