package edu.arizona.biosemantics.etcsitehelpcenter.client.layout;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public interface IEtcSiteHelpView extends IsWidget {

	public interface Presenter {

		IEtcSiteHelpView getView();
	}
	
	void setContent(IsWidget content);
	void setPresenter(Presenter presenter);
	SimpleLayoutPanel getContentContainer();
}