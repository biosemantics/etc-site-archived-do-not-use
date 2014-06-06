package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.user.client.ui.IsWidget;

public interface ILoginTopView extends IsWidget {

	public interface Presenter {
		void onHome();
		
		void onAbout();
		
		void onHelp();

		void onNews();
		
		
		void onLogin();
		
		void onRegister();
		
	}

	void setPresenter(Presenter presenter);
	
}
