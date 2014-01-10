package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.user.client.ui.IsWidget;

public interface ILoginTopView extends IsWidget {

	public interface Presenter {

		void onHelp();

		void onLogin();
		
	}

	void setPresenter(Presenter presenter);

	String getUser();

	String getPassword();
	
}
