package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.client.help.IHelpSemanticMarkupView.Presenter;

public interface IHelpSemanticMarkupView {

	public interface Presenter {
		
	}


	IsWidget asWidget();


	void setPresenter(Presenter presenter);
	
}
