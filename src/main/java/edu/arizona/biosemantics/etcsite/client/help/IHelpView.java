package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.user.client.ui.IsWidget;

public interface IHelpView {

	public interface Presenter {
		
	}

	IsWidget asWidget();

	void setPresenter(Presenter presenter);

	void setContent(String html);
	
}
