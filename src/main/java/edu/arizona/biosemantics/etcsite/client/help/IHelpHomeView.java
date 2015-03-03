package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.user.client.ui.IsWidget;

public interface IHelpHomeView {

	public interface Presenter {
		
	}

	IsWidget asWidget();

	void setPresenter(Presenter presenter);
	
}
